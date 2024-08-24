package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TimeEventHandler(
    private val redisTimeEventRepository: RedisTimeEventRepository
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(TimeEventHandler::class.java)
    }

    @Transactional
    fun createEvent(
        game: RedisGame,
        eventType: RedisTimeEventType,
        sourceUser: RedisGameUser? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        createEvent(game.gameId!!, eventType, game.globalTimer, sourceUser, location, timeLeft)
    }

    @Transactional
    fun createEvent(
        game: GameSession,
        eventType: RedisTimeEventType,
        startDateTime: Long,
        sourceUser: RedisGameUser? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        createEvent(game.id!!, eventType, startDateTime, sourceUser, location, timeLeft)
    }

    @Transactional
    fun createEvent(
        gameId: Long,
        eventType: RedisTimeEventType,
        startDateTime: Long,
        sourceUser: RedisGameUser? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        createEvent(gameId, eventType, startDateTime, sourceUser?.userId, location, timeLeft)
    }

    @Transactional
    fun createEvent(
        gameId: Long,
        eventType: RedisTimeEventType,
        startDateTime: Long,
        sourceUserId: Long? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        val timer = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            timeStart = startDateTime,
            timePast = 0L,
            timeLeft = timeLeft ?: eventType.getDefaultTime(),
            sourceUserId = sourceUserId,
            type = eventType,
            state = RedisTimeEventState.ACTIVE,
            xLocation = location?.first,
            yLocation = location?.second
        )
        redisTimeEventRepository.save(timer)
    }

    @Transactional
    fun tryToDeleteEvent(
        eventType: RedisTimeEventType,
        allEvents: List<RedisTimeEvent>
    ) {
        logger.info("deleting ${eventType.name} event")
        allEvents.filter {
            it.type == eventType
        }.forEach {
            it.timePast += it.timeLeft
            it.timeLeft = 0
            it.state = RedisTimeEventState.PAST
            redisTimeEventRepository.delete(it)
        }
    }

    fun pushEvent(ritualEvent: RedisTimeEvent, timeToAdd: Long) {
        ritualEvent.timePast += timeToAdd
        ritualEvent.timeLeft -= timeToAdd
        redisTimeEventRepository.save(ritualEvent)
    }
}