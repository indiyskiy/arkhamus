package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.logic.Location
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual.GodVoteCastRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.redis.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RitualHandler(
    private val eventHandler: TimeEventHandler,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val godVoteHandler: GodVoteHandler,
    private val godToCorkResolver: GodToCorkResolver,
    private val recipesSource: RecipesSource,
    private val activityHandler: ActivityHandler
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualHandler::class.java)
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
        GodVoteCastRequestProcessor.logger.info("tryToForceStart")
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
        altarHolder?.lockedGodId = quorum.getId()
        altarHolder?.itemsForRitual = recipe.ingredients.associate {
            it.item.id to it.number
        }
        altarHolder?.itemsOnAltars = recipe.ingredients.associate {
            it.item.id to 0
        }
        altarHolder?.itemsIdToAltarId = recipe.ingredients.mapIndexed { index, ingredient ->
            ingredient.item.id to altars[index].inGameId()
        }.toMap()

        altarHolder?.state = MapAltarState.GOD_LOCKED
        altarHolder?.let { redisAltarHolderRepository.save(it) }

        eventHandler.createEvent(
            game, RedisTimeEventType.RITUAL_GOING
        )
    }

    fun unlockTheGod(altarHolder: RedisAltarHolder?) {
        altarHolder?.lockedGodId = null
        altarHolder?.itemsForRitual = emptyMap()
        altarHolder?.itemsIdToAltarId = emptyMap()
        altarHolder?.itemsOnAltars = emptyMap()
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
}