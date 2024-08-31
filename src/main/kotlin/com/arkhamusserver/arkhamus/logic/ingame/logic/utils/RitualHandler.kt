package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.ritual.GodVoteCastRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarHolderRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarPollingRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
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
    private val generalVoteHandler: GeneralVoteHandler,
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
        val canVote = generalVoteHandler.userCanPossiblyVote(allUsers)
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

    @Transactional
    fun failRitual(
        altarHolder: RedisAltarHolder?,
        altarPolling: RedisAltarPolling,
        events: List<RedisTimeEvent>,
        game: RedisGame
    ) {
        eventHandler.tryToDeleteEvent(RedisTimeEventType.ALTAR_VOTING, events)

        logger.info("removing polling - fail ritual")
        altarPolling.state = MapAltarPollingState.FAILED
        redisAltarPollingRepository.delete(altarPolling)

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
        altarPolling: RedisAltarPolling,
        altarHolder: RedisAltarHolder?
    ): RedisAltarHolder? {
        redisAltarPollingRepository.delete(altarPolling)
        unlockTheGod(altarHolder)
        altarHolder?.state = MapAltarState.OPEN
        return altarHolder?.let { redisAltarHolderRepository.save(altarHolder) }
    }

    @Transactional
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
                failRitual(
                    altarHolder,
                    altarPolling,
                    events,
                    game
                )
            }
        }
    }

    @Transactional
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
            ingredient.item.id to altars[index].altarId
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
}