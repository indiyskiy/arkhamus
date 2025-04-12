package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TeleportHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag.IN_RITUAL
import com.arkhamusserver.arkhamus.model.ingame.*
import org.apache.commons.lang3.math.NumberUtils.min
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RitualHandler(
    private val eventHandler: TimeEventHandler,
    private val inGameAltarPollingRepository: InGameAltarPollingRepository,
    private val godVoteHandler: GodVoteHandler,
    private val godToCorkResolver: GodToCorkResolver,
    private val recipesSource: RecipesSource,
    private val activityHandler: ActivityHandler,
    private val gameEndLogic: GameEndLogic,
    private val inventoryHandler: InventoryHandler,
    private val inGameAltarHolderRepository: InGameAltarHolderRepository,
    private val madnessHandler: UserMadnessHandler,
    private val teleportHandler: TeleportHandler,
    private val geometryUtils: GeometryUtils
) {
    companion object {
        const val MADNESS_PER_USER = GlobalGameSettings.MAX_USER_MADNESS / 12.0
        var logger: Logger = LoggerFactory.getLogger(RitualHandler::class.java)
    }

    @Transactional
    fun godVoteStart(
        globalGameData: GlobalGameData,
        altar: InGameAltar,
        god: God,
        currentGameUser: InGameUser,
        allUsers: Collection<InGameUser>,
        altars: List<InGameAltar>,
        altarHolder: InGameAltarHolder?,
        events: List<InGameTimeEvent>,
        game: InRamGame
    ) {
        createGodVoteStartProcess(
            gameId = game.gameId,
            globalTimer = globalGameData.game.globalTimer,
            sourceUserId = currentGameUser.inGameId(),
            altar = altar
        )
        val altarPolling = createGodVote(
            god = god,
            altar = altar,
            globalGameData = globalGameData,
            currentGameUser = currentGameUser,
        )
        globalGameData.altarHolder?.state = MapAltarState.VOTING
        globalGameData.altarHolder?.let { inGameAltarHolderRepository.save(it) }

        activityHandler.addUserWithTargetActivity(
            game.inGameId(),
            ActivityType.ALTAR_VOTE_STARTED,
            currentGameUser,
            game.globalTimer,
            GameObjectType.ALTAR,
            altar,
            god.getId().toLong(),
        )

        tryToForceStartRitual(allUsers, altarPolling, altars, altarHolder, events, game)
    }

    fun failRitualByTime(
        altarHolder: InGameAltarHolder?,
        globalGameData: GlobalGameData
    ) {
        failRitualStartCooldown(
            altarHolder,
            globalGameData.altarPolling,
            globalGameData.timeEvents,
            globalGameData.game
        )
        logger.info("ritual failed")
        globalGameData
            .users
            .values
            .filter { user ->
                user.stateTags.contains(IN_RITUAL)
            }
            .forEach { user ->
                user.stateTags -= IN_RITUAL
            }
        logger.info("users from ritual removed")
    }

    fun countItemsNotches(
        ritualEvent: InGameTimeEvent?,
        altarHolder: InGameAltarHolder?,
    ): List<ItemNotch> {
        if (ritualEvent == null) return emptyList()
        if (altarHolder == null) return emptyList()
        val start = ritualEvent.timeStart
        val size = altarHolder.itemsForRitual.size
        if (size == 0) return emptyList()
        val step = (ritualEvent.timePast + ritualEvent.timeLeft) / size
        return altarHolder.itemsForRitual
            .toList()
            .sortedBy { it.first.id }
            .mapIndexed { index, (item, _) ->
                ItemNotch().apply {
                    this.index = index
                    this.item = item
                    this.gameTimeStart = start + (step * index)
                    this.gameTimeEnd = start + (step * (index + 1))
                    this.altarId = altarHolder.itemsToAltarId[item]!!
                }
            }.sortedBy { it.index }
    }

    fun countCurrentNotch(gameTimeItemsNotches: List<ItemNotch>, currentGameTime: Long): ItemNotch? {
        return gameTimeItemsNotches.firstOrNull {
            it.gameTimeStart <= currentGameTime &&
                    it.gameTimeEnd > currentGameTime
        }
    }

    fun takeItemForRitual(
        item: Item,
        itemNumber: Int,
        altarHolder: InGameAltarHolder?,
        user: InGameUser
    ) {
        val itemsNeeded = (altarHolder?.itemsForRitual?.get(item) ?: 0) -
                (altarHolder?.itemsOnAltars?.get(item) ?: 0)
        val itemsInInventory = inventoryHandler.howManyItems(user, item)
        val itemsToTake = min(itemNumber, itemsNeeded, itemsInInventory)
        if (itemsToTake > 0) {
            val newItemMap = if (altarHolder?.itemsOnAltars?.contains(item) == true) {
                altarHolder.itemsOnAltars.map {
                    if (it.key == item) {
                        it.key to (it.value + itemsToTake)
                    } else {
                        it.key to it.value
                    }
                }.toMap()
            } else {
                mapOf(item to itemsToTake)
            }
            altarHolder?.itemsOnAltars = newItemMap
            altarHolder?.thmAddedThisRound = true
            altarHolder?.let { inGameAltarHolderRepository.save(it) }

            inventoryHandler.consumeItems(user, item, itemsToTake)
        }
    }

    fun processAllItemsPut(
        globalGameData: GlobalGameData,
        altarHolder: InGameAltarHolder,
        ongoingEvents: List<OngoingEvent>
    ) {
        logger.info("put all items")
        if (globalGameData.game.god == altarHolder.lockedGod) {
            gameEndLogic.endTheGame(
                globalGameData.game,
                globalGameData.users,
                GameEndReason.RITUAL_SUCCESS
            )
        } else {
            justFinishRitual(ongoingEvents)
        }
    }

    fun isAllItemsPut(altarHolder: InGameAltarHolder): Boolean =
        altarHolder.itemsForRitual.all { (itemId, number) ->
            altarHolder.itemsOnAltars[itemId] == number
        }

    fun tryToPushEvent(
        altarHolder: InGameAltarHolder?,
        ongoingEvent: InGameTimeEvent,
        currentNotch: ItemNotch
    ) {
        val item = currentNotch.item!!
        if (thisItemIsPutOnAltar(altarHolder, item)) {
            pushEvent(ongoingEvent, currentNotch)
        }
    }

    fun kickUsersFromRitual(
        holder: InGameAltarHolder,
        data: GlobalGameData
    ) {
        val usersInRitual = holder.usersInRitual
        val usersToKick = holder.usersToKick.intersect(usersInRitual)
        if (usersToKick.isEmpty()) return

        val isKickForAll = usersToKick.size == usersInRitual.size
        val madnessApply = if (isKickForAll) {
            0.0
        } else {
            MADNESS_PER_USER * usersInRitual.size / usersToKick.size
        }
        val ritualThresholds = data.thresholds.filter { it.type == ThresholdType.RITUAL }
        usersToKick.forEach { userId ->
            val user = data.users[userId]!!
            val nearestPoint = geometryUtils.nearestPoint(user, ritualThresholds)
            nearestPoint?.let {
                madnessHandler.tryApplyMadness(
                    user,
                    madnessApply,
                    data.game.globalTimer,
                    InGameUserStatus.RITUAL_KICK,
                    data
                )
                teleportHandler.forceTeleport(data.game, user, it)
                user.stateTags -= IN_RITUAL
            }
        }
        holder.usersToKick = emptySet()
        holder.usersInRitual = usersInRitual.filter { it !in usersToKick }.toSet()
        if (holder.usersInRitual.isEmpty()) {
            failRitualStartCooldown(
                holder,
                data.altarPolling,
                data.timeEvents,
                data.game
            )
        }
        inGameAltarHolderRepository.save(holder)
    }

    @Transactional
    fun castGodVote(
        god: God,
        altar: InGameAltar,
        currentGameUser: InGameUser,
        altarPolling: InGameAltarPolling,
        gameData: GodVoteCastRequestProcessData,
        altarHolder: InGameAltarHolder?,
        altars: List<InGameAltar>,
        game: InRamGame,
        allUsers: Collection<InGameUser>,
        events: List<InGameTimeEvent>
    ) {
        val userId: Long = gameData.gameUser!!.inGameId()
        val godId = god.getId()
        altarPolling.userVotes[userId] = godId
        inGameAltarPollingRepository.save(altarPolling)

        activityHandler.addUserWithTargetActivity(
            game.inGameId(),
            ActivityType.ALTAR_VOTE_CASTED,
            currentGameUser,
            game.globalTimer,
            GameObjectType.ALTAR,
            altar,
            god.getId().toLong(),
        )

        tryToForceStartRitual(allUsers, altarPolling, altars, altarHolder, events, game)
    }

    fun gotQuorum(
        allUsers: Collection<InGameUser>,
        altarPolling: InGameAltarPolling
    ): God? {
        val canVote = godVoteHandler.usersCanPossiblyVote(allUsers)
        val canVoteIdsSet = canVote.map { it.inGameId() }.toSet()
        val votesStillRelevant = altarPolling.userVotes.filter { it.key in canVoteIdsSet }
        val votedUserIdsSet = votesStillRelevant.map { it.key }.toSet()

        val skipped = altarPolling.skippedUsers.filter { it in canVoteIdsSet }.toSet()
        val notVoted = canVoteIdsSet.filter {
            it !in votedUserIdsSet &&
                    it !in skipped
        }

        val votesCounter = votesStillRelevant
            .map { it.key to it.value }
            .groupBy { it.second }
            .map { it.key to it.value.size }
        val maxVoteCounter = votesCounter.maxBy { it.second }.second
        if (notVoted.size > maxVoteCounter) {
            return null
        }
        val godsWithMaxVotes = votesCounter
            .filter { it.second == maxVoteCounter }
            .map { vote -> God.values().first { it.getId() == vote.first } }
        return if (godsWithMaxVotes.size > 1) {
            null
        } else {
            godsWithMaxVotes.first()
        }
    }

    fun failRitualStartCooldown(
        altarHolder: InGameAltarHolder?,
        altarPolling: InGameAltarPolling?,
        events: List<InGameTimeEvent>,
        game: InRamGame
    ) {
        eventHandler.tryToDeleteEvent(InGameTimeEventType.ALTAR_VOTING, events)
        eventHandler.tryToDeleteEvent(InGameTimeEventType.RITUAL_GOING, events)

        altarPolling?.let {
            logger.info("removing polling - fail ritual")
            it.state = MapAltarPollingState.FAILED
            inGameAltarPollingRepository.delete(it)
        }

        if (altarHolder != null) {
            unlockTheGod(altarHolder)
            altarHolder.state = MapAltarState.LOCKED
            altarHolder.usersInRitual = emptySet()
            altarHolder.usersToKick = emptySet()
            inGameAltarHolderRepository.save(altarHolder)
        }
        logger.info("creating COOLDOWN event")
        eventHandler.createEvent(
            game,
            InGameTimeEventType.ALTAR_VOTING_COOLDOWN,
        )
    }

    @Transactional
    fun finishAltarPolling(
        altarPolling: InGameAltarPolling?,
        altarHolder: InGameAltarHolder?
    ): InGameAltarHolder? {
        altarPolling?.let { inGameAltarPollingRepository.delete(it) }
        return altarHolder?.let {
            unlockTheGod(it)
            it.state = MapAltarState.OPEN
            inGameAltarHolderRepository.save(it)
        }
    }

    fun tryToForceStartRitual(
        allUsers: Collection<InGameUser>,
        altarPolling: InGameAltarPolling,
        altars: List<InGameAltar>,
        altarHolder: InGameAltarHolder?,
        events: List<InGameTimeEvent>,
        game: InRamGame
    ) {
        logger.info("tryToForceStart")
        if (godVoteHandler.everybodyVoted(allUsers, altarPolling)) {
            val quorum = gotQuorum(allUsers, altarPolling)
            if (quorum != null) {
                lockTheGod(
                    quorum = quorum,
                    altars = altars,
                    altarPolling = altarPolling,
                    altarHolder = altarHolder,
                    events = events,
                    game = game
                )
            } else {
                failRitualStartCooldown(
                    altarHolder,
                    altarPolling,
                    events,
                    game
                )
            }
        }
    }

    fun lockTheGod(
        quorum: God,
        altars: List<InGameAltar>,
        altarPolling: InGameAltarPolling,
        altarHolder: InGameAltarHolder?,
        events: List<InGameTimeEvent>,
        game: InRamGame
    ) {
        val sortedAltars = altars.sortedBy { it.inGameId() }
        eventHandler.tryToDeleteEvent(InGameTimeEventType.ALTAR_VOTING, events)

        logger.info("removing polling - god locked")
        altarPolling.state = MapAltarPollingState.FIXED
        inGameAltarPollingRepository.delete(altarPolling)

        val cork = godToCorkResolver.resolve(quorum)
        val recipe = recipesSource.getAllRecipes().first { it.item == cork }
        altarHolder?.lockedGod = quorum
        altarHolder?.thmAddedThisRound = false
        altarHolder?.round = 0
        val itemsSorted = recipe.ingredients.sortedBy { it.item.id }
        altarHolder?.itemsForRitual = itemsSorted.associate {
            it.item to it.number
        }
        altarHolder?.itemsOnAltars = itemsSorted.associate {
            it.item to 0
        }
        altarHolder?.itemsToAltarId = itemsSorted.mapIndexed { index, ingredient ->
            ingredient.item to sortedAltars[index].inGameId()
        }.toMap()

        altarHolder?.state = MapAltarState.GOD_LOCKED
        altarHolder?.let { inGameAltarHolderRepository.save(it) }

        eventHandler.createEvent(
            game, InGameTimeEventType.RITUAL_GOING
        )
    }

    fun unlockTheGod(altarHolder: InGameAltarHolder) {
        altarHolder.lockedGod = null
        altarHolder.thmAddedThisRound = false
        altarHolder.round = 0
        altarHolder.itemsForRitual = emptyMap()
        altarHolder.itemsToAltarId = emptyMap()
        altarHolder.itemsOnAltars = emptyMap()
        altarHolder.currentStepItem = null
    }

    fun startAnotherRound(
        data: GlobalGameData,
        holder: InGameAltarHolder
    ) {
        holder.round++
        holder.thmAddedThisRound = false
        inGameAltarHolderRepository.save(holder)
        eventHandler.createEvent(
            data.game, InGameTimeEventType.RITUAL_GOING
        )
    }

    private fun createGodVote(
        god: God,
        altar: InGameAltar,
        globalGameData: GlobalGameData,
        currentGameUser: InGameUser,
    ): InGameAltarPolling {
        val userId: Long = currentGameUser.inGameId()
        val godId = god.getId()
        val altarPolling = InGameAltarPolling(
            id = generateRandomId(),
            started = globalGameData.game.globalTimer,
            altarId = altar.inGameId(),
            gameId = globalGameData.game.gameId,
            startedUserId = userId,
            userVotes = mutableMapOf(userId to godId),
            state = MapAltarPollingState.ONGOING
        )
        inGameAltarPollingRepository.save(altarPolling)
        return altarPolling
    }

    private fun createGodVoteStartProcess(
        gameId: Long,
        globalTimer: Long,
        sourceUserId: Long,
        altar: InGameAltar
    ) {
        eventHandler.createEvent(
            gameId = gameId,
            eventType = InGameTimeEventType.ALTAR_VOTING,
            startDateTime = globalTimer,
            sourceObjectId = sourceUserId,
            location = Location(altar.x, altar.y, altar.z),
        )
    }

    private fun findRitualEvent(ongoingEvents: List<OngoingEvent>) =
        ongoingEvents.filter {
            it.event.type == InGameTimeEventType.RITUAL_GOING &&
                    it.event.state == InGameTimeEventState.ACTIVE
        }

    private fun justFinishRitual(
        ongoingEvents: List<OngoingEvent>
    ) {
        findRitualEvent(ongoingEvents).forEach { event ->
            val ritualEvent = event.event
            val timeToAdd = ritualEvent.timeLeft - 1
            if (timeToAdd > 0) {
                eventHandler.pushEvent(ritualEvent, timeToAdd)
            }
        }
    }

    private fun pushEvent(
        ritualEvent: InGameTimeEvent,
        currentNotch: ItemNotch
    ) {
        logger.info("trying to push event")
        logger.info("trying to push event: current notch: $currentNotch")
        val timeToAdd = currentNotch.gameTimeEnd - ritualEvent.currentEventTime()
        logger.info("trying to push event: timeToAdd: $timeToAdd")
        if (timeToAdd > 0) {
            logger.info("trying to push event: push me baby")
            eventHandler.pushEvent(ritualEvent, timeToAdd)
        }
    }

    private fun thisItemIsPutOnAltar(
        altarHolder: InGameAltarHolder?,
        item: Item
    ) = (altarHolder?.itemsOnAltars?.get(item) ?: 0) >= (altarHolder?.itemsForRitual?.get(item) ?: 0)

}