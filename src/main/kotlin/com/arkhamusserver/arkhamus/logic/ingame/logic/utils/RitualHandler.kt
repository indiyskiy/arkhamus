package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual.GodVoteCastRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.redis.*
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RitualHandler(
    private val timeEventRepository: RedisTimeEventRepository,
    private val redisAltarPollingRepository: RedisAltarPollingRepository,
    private val redisAltarHolderRepository: RedisAltarHolderRepository,
    private val godVoteHandler: GodVoteHandler,
    private val godToCorkResolver: GodToCorkResolver,
    private val recipesSource: RecipesSource
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(RitualHandler::class.java)
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

    fun failRitual(
        altarHolder: RedisAltarHolder,
        altarPolling: RedisAltarPolling,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ): RedisTimeEvent {
        altarPolling.state = MapAltarPollingState.FAILED
        redisAltarPollingRepository.save(altarPolling)

        altarHolder.state = MapAltarState.LOCKED
        redisAltarHolderRepository.save(altarHolder)

        val cooldownEvent = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = game.gameId!!,
            sourceUserId = null,
            targetUserId = null,
            timeStart = game.globalTimer,
            timePast = 0L,
            timeLeft = RedisTimeEventType.ALTAR_VOTING_COOLDOWN.getDefaultTime(),
            type = RedisTimeEventType.ALTAR_VOTING_COOLDOWN,
            state = RedisTimeEventState.ACTIVE,
            xLocation = null,
            yLocation = null,
        )
        tryToDeleteEvent(RedisTimeEventType.ALTAR_VOTING, events)
        return timeEventRepository.save(cooldownEvent)
    }


    fun finishAltarPolling(
        altarPolling: RedisAltarPolling,
        altarHolder: RedisAltarHolder
    ): RedisAltarHolder {
        redisAltarPollingRepository.delete(altarPolling)
        unlockTheGod(altarHolder)
        altarHolder.state = MapAltarState.OPEN
        return redisAltarHolderRepository.save(altarHolder)
    }

    fun tryToForceStartRitual(
        allUsers: Collection<RedisGameUser>,
        altarPolling: RedisAltarPolling,
        altars: Map<Long, RedisAltar>,
        altarHolder: RedisAltarHolder,
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
                    altarHolder = altarHolder,
                    events = events,
                    game = game
                )
            } else {
                failRitual(
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
        altarHolder: RedisAltarHolder,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ): RedisTimeEvent {
        logger.info("locking the god $quorum")
        val cork = godToCorkResolver.resolve(quorum)
        logger.info("creating altars for  $cork")
        val recipe = recipesSource.getAllRecipes().first { it.item == cork }
        logger.info("recipe for $cork has id ${recipe.recipeId}")
        altarHolder.lockedGodId = quorum.getId()
        altarHolder.itemsForRitual = recipe.ingredients.associate {
            it.item.id to it.number
        }
        logger.info("items for ritual ${altarHolder.itemsForRitual.size}")
        altarHolder.itemsOnAltars = recipe.ingredients.associate {
            it.item.id to 0
        }
        altarHolder.itemsIdToAltarId = recipe.ingredients.mapIndexed { index, ingredient ->
            ingredient.item.id to altars[index].altarId
        }.toMap()

        altarHolder.state = MapAltarState.GOD_LOCKED
        redisAltarHolderRepository.save(altarHolder)
        logger.info("god lock finished")

        tryToDeleteEvent(RedisTimeEventType.ALTAR_VOTING, events)

        val ritualGoingEvent = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = game.gameId!!,
            sourceUserId = null,
            targetUserId = null,
            timeStart = game.globalTimer,
            timePast = 0L,
            timeLeft = RedisTimeEventType.RITUAL_GOING.getDefaultTime(),
            type = RedisTimeEventType.RITUAL_GOING,
            state = RedisTimeEventState.ACTIVE,
            xLocation = null,
            yLocation = null,
        )
        return timeEventRepository.save(ritualGoingEvent)
    }

    private fun tryToDeleteEvent(
        eventType: RedisTimeEventType,
        allEvents: List<RedisTimeEvent>
    ) {
        allEvents.filter {
            it.type == eventType
        }.forEach {
            it.state = RedisTimeEventState.PAST
            timeEventRepository.delete(it)
        }
    }

    fun unlockTheGod(altarHolder: RedisAltarHolder) {
        altarHolder.lockedGodId = null
        altarHolder.itemsForRitual = emptyMap()
        altarHolder.itemsIdToAltarId = emptyMap()
        altarHolder.itemsOnAltars = emptyMap()
    }
}