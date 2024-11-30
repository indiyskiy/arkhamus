package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag.IN_RITUAL
import com.arkhamusserver.arkhamus.model.redis.*
import org.apache.commons.lang3.math.NumberUtils.min
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RitualHandler(
    private val eventHandler: TimeEventHandler,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val godVoteHandler: GodVoteHandler,
    private val godToCorkResolver: GodToCorkResolver,
    private val recipesSource: RecipesSource,
    private val activityHandler: ActivityHandler,
    private val gameEndLogic: GameEndLogic,
    private val inventoryHandler: InventoryHandler,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualHandler::class.java)
    }

    fun failRitualByTime(
        altarHolder: RedisAltarHolder?,
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
                user.stateTags.contains(IN_RITUAL.name)
            }
            .forEach { user ->
                user.stateTags -= IN_RITUAL.name
            }
        logger.info("users from ritual removed")
    }

    fun countItemsNotches(
        ritualEvent: RedisTimeEvent?,
        altarHolder: RedisAltarHolder?,
    ): List<ItemNotch> {
        if (ritualEvent == null) return emptyList()
        if (altarHolder == null) return emptyList()
        val start = ritualEvent.timeStart
        val size = altarHolder.itemsForRitual.size
        val step = (ritualEvent.timePast + ritualEvent.timeLeft) / size
        return altarHolder.itemsForRitual
            .toList()
            .sortedBy { it.first }
            .mapIndexed { index, (item, _) ->
                ItemNotch().apply {
                    this.item = item
                    this.gameTimeStart = start + (step * index)
                    this.gameTimeEnd = start + (step * (index + 1))
                }
            }
    }

    fun countCurrentItem(gameTimeItemsNotches: List<ItemNotch>, currentGameTime: Long): Item? {
        return gameTimeItemsNotches.firstOrNull {
            it.gameTimeStart <= currentGameTime &&
                    it.gameTimeEnd > currentGameTime
        }?.item
    }

    fun takeItemForRitual(
        item: Item,
        itemNumber: Int,
        altarHolder: RedisAltarHolder?,
        user: RedisGameUser
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
            altarHolder?.let { redisAltarHolderRepository.save(it) }

            inventoryHandler.consumeItems(user, item, itemsToTake)
        }
    }

    fun processAllItemsPut(
        globalGameData: GlobalGameData,
        altarHolder: RedisAltarHolder,
        ongoingEvents: List<OngoingEvent>
    ) {
        logger.info("put all item")
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

    fun isAllItemsPut(altarHolder: RedisAltarHolder): Boolean =
        altarHolder.itemsForRitual.all { (itemId, number) ->
            altarHolder.itemsOnAltars[itemId] == number
        }

    fun tryToShiftTime(
        altarHolder: RedisAltarHolder?,
        item: Item,
        ongoingEvents: List<OngoingEvent>
    ) {
        logger.info("trying to shift time for all possible events")
        if (thisItemIsPutOnAltar(altarHolder, item)) {
            shiftTimeOfEvent(ongoingEvents, item, altarHolder)
        }
    }

    fun tryToShiftTime(
        altarHolder: RedisAltarHolder?,
        item: Item,
        ongoingEvent: RedisTimeEvent
    ) {
        logger.info("trying to shift time for one event")
        if (thisItemIsPutOnAltar(altarHolder, item)) {
            shiftTimeOfEvent(ongoingEvent, item, altarHolder)
        }
    }

    @Transactional
    fun godVoteStart(
        globalGameData: GlobalGameData,
        altar: RedisAltar,
        god: God,
        currentGameUser: RedisGameUser,
        allUsers: Collection<RedisGameUser>,
        altars: Map<Long, RedisAltar>,
        altarHolder: RedisAltarHolder?,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ) {
        createGodVoteStartProcess(
            gameId = game.gameId!!,
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
        globalGameData.altarHolder?.let { redisAltarHolderRepository.save(it) }

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

    @Transactional
    fun castGodVote(
        god: God,
        altar: RedisAltar,
        currentGameUser: RedisGameUser,
        altarPolling: RedisAltarPolling,
        gameData: GodVoteCastRequestProcessData,
        altarHolder: RedisAltarHolder?,
        altars: Map<Long, RedisAltar>,
        game: RedisGame,
        allUsers: Collection<RedisGameUser>,
        events: List<RedisTimeEvent>
    ) {
        val userId: Long = gameData.gameUser!!.userId
        val godId = god.getId()
        altarPolling.userVotes[userId] = godId
        redisAltarPollingRepository.save(altarPolling)

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
        allUsers: Collection<RedisGameUser>,
        altarPolling: RedisAltarPolling
    ): God? {
        val canVote = godVoteHandler.usersCanPossiblyVote(allUsers)
        val canVoteIdsSet = canVote.map { it.userId }.toSet()
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
        altarHolder: RedisAltarHolder?,
        altarPolling: RedisAltarPolling?,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ) {
        eventHandler.tryToDeleteEvent(RedisTimeEventType.ALTAR_VOTING, events)

        altarPolling?.let {
            logger.info("removing polling - fail ritual")
            it.state = MapAltarPollingState.FAILED
            redisAltarPollingRepository.delete(it)
        }

        unlockTheGod(altarHolder)
        altarHolder?.state = MapAltarState.LOCKED
        altarHolder?.let { redisAltarHolderRepository.save(it) }
        logger.info("creating COOLDOWN event")
        eventHandler.createEvent(
            game,
            RedisTimeEventType.ALTAR_VOTING_COOLDOWN,
        )
    }

    @Transactional
    fun finishAltarPolling(
        altarPolling: RedisAltarPolling?,
        altarHolder: RedisAltarHolder?
    ): RedisAltarHolder? {
        altarPolling?.let { redisAltarPollingRepository.delete(it) }
        unlockTheGod(altarHolder)
        altarHolder?.state = MapAltarState.OPEN
        return altarHolder?.let { redisAltarHolderRepository.save(altarHolder) }
    }

    fun tryToForceStartRitual(
        allUsers: Collection<RedisGameUser>,
        altarPolling: RedisAltarPolling,
        altars: Map<Long, RedisAltar>,
        altarHolder: RedisAltarHolder?,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ) {
        logger.info("tryToForceStart")
        if (godVoteHandler.everybodyVoted(allUsers, altarPolling)) {
            val quorum = gotQuorum(allUsers, altarPolling)
            if (quorum != null) {
                lockTheGod(
                    quorum = quorum,
                    altars = altars.values.toList(),
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
        altars: List<RedisAltar>,
        altarPolling: RedisAltarPolling,
        altarHolder: RedisAltarHolder?,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ) {
        eventHandler.tryToDeleteEvent(RedisTimeEventType.ALTAR_VOTING, events)

        logger.info("removing polling - god locked")
        altarPolling.state = MapAltarPollingState.FIXED
        redisAltarPollingRepository.delete(altarPolling)

        val cork = godToCorkResolver.resolve(quorum)
        val recipe = recipesSource.getAllRecipes().first { it.item == cork }
        altarHolder?.lockedGod = quorum
        altarHolder?.thmAddedThisRound = false
        altarHolder?.round = 0
        altarHolder?.itemsForRitual = recipe.ingredients.associate {
            it.item to it.number
        }
        altarHolder?.itemsOnAltars = recipe.ingredients.associate {
            it.item to 0
        }
        altarHolder?.itemsToAltarId = recipe.ingredients.mapIndexed { index, ingredient ->
            ingredient.item to altars[index].inGameId()
        }.toMap()

        altarHolder?.state = MapAltarState.GOD_LOCKED
        altarHolder?.let { redisAltarHolderRepository.save(it) }

        eventHandler.createEvent(
            game, RedisTimeEventType.RITUAL_GOING
        )
    }

    fun unlockTheGod(altarHolder: RedisAltarHolder?) {
        altarHolder?.lockedGod = null
        altarHolder?.thmAddedThisRound = false
        altarHolder?.round = 0
        altarHolder?.itemsForRitual = emptyMap()
        altarHolder?.itemsToAltarId = emptyMap()
        altarHolder?.itemsOnAltars = emptyMap()
    }

    fun startAnotherRound(
        data: GlobalGameData,
        holder: RedisAltarHolder
    ) {
        holder.round++
        redisAltarHolderRepository.save(holder)
        eventHandler.createEvent(
            data.game, RedisTimeEventType.RITUAL_GOING
        )
    }

    private fun createGodVote(
        god: God,
        altar: RedisAltar,
        globalGameData: GlobalGameData,
        currentGameUser: RedisGameUser,
    ): RedisAltarPolling {
        val userId: Long = currentGameUser.userId
        val godId = god.getId()
        val altarPolling = RedisAltarPolling(
            id = generateRandomId(),
            started = globalGameData.game.globalTimer,
            altarId = altar.inGameId(),
            gameId = globalGameData.game.gameId!!,
            startedUserId = userId,
            userVotes = mutableMapOf(userId to godId),
            state = MapAltarPollingState.ONGOING
        )
        redisAltarPollingRepository.save(altarPolling)
        return altarPolling
    }

    private fun createGodVoteStartProcess(
        gameId: Long,
        globalTimer: Long,
        sourceUserId: Long,
        altar: RedisAltar
    ) {
        eventHandler.createEvent(
            gameId = gameId,
            eventType = RedisTimeEventType.ALTAR_VOTING,
            startDateTime = globalTimer,
            sourceObjectId = sourceUserId,
            location = Location(altar.x, altar.y, altar.z),
        )
    }

    private fun findRitualEvent(ongoingEvents: List<OngoingEvent>) =
        ongoingEvents.filter {
            it.event.type == RedisTimeEventType.RITUAL_GOING &&
                    it.event.state == RedisTimeEventState.ACTIVE
        }

    private fun shiftTimeOfEvent(
        event: RedisTimeEvent,
        item: Item,
        altarHolder: RedisAltarHolder?
    ) {
        pushEvent(event, altarHolder, item)
    }

    private fun shiftTimeOfEvent(
        ongoingEvents: List<OngoingEvent>,
        item: Item,
        altarHolder: RedisAltarHolder?
    ) {
        findRitualEvent(ongoingEvents).forEach { event ->
            pushEvent(event.event, altarHolder, item)
        }
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
        ritualEvent: RedisTimeEvent,
        altarHolder: RedisAltarHolder?,
        item: Item
    ) {
        logger.info("shift time")
        val notches = countItemsNotches(ritualEvent, altarHolder)
        val notchOfCurrentItem = notches.firstOrNull { it.item == item }
        if (notchOfCurrentItem != null) {
            val timeToAdd = notchOfCurrentItem.gameTimeEnd - (ritualEvent.timeStart + ritualEvent.timePast)
            if (timeToAdd > 0) {
                eventHandler.pushEvent(ritualEvent, timeToAdd)
            }
        }
    }

    private fun thisItemIsPutOnAltar(
        altarHolder: RedisAltarHolder?,
        item: Item
    ) = (altarHolder?.itemsOnAltars?.get(item) ?: 0) >= (altarHolder?.itemsForRitual?.get(item) ?: 0)

}