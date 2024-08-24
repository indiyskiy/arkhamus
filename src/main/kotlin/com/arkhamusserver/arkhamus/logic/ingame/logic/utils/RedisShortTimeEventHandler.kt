package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RedisShortTimeEventHandler(
    private val redisShortTimeEventRepository: RedisShortTimeEventRepository
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(RedisShortTimeEventHandler::class.java)
    }

    @Transactional
    fun createShortTimeEvent(
        gameId: Long,
        eventType: ShortTimeEventType,
        startDateTime: Long,
        sourceUser: RedisGameUser? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        createShortTimeEvent(gameId, eventType, startDateTime, sourceUser?.userId, location, timeLeft)
    }

    @Transactional
    fun createShortTimeEvent(
        gameId: Long,
        eventType: ShortTimeEventType,
        startDateTime: Long,
        sourceId: Long? = null,
        location: Pair<Double, Double>? = null,
        timeLeft: Long? = null
    ) {
        val timer = RedisShortTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = gameId,
            timeStart = startDateTime,
            timePast = 0L,
            timeLeft = timeLeft ?: eventType.getTime(),
            sourceId = sourceId,
            type = eventType,
            state = RedisTimeEventState.ACTIVE,
            xLocation = location?.first,
            yLocation = location?.second,
        )
        redisShortTimeEventRepository.save(timer)
    }

    fun tryToDeleteEvent(
        eventType: ShortTimeEventType,
        allEvents: List<RedisShortTimeEvent>
    ) {
        logger.info("deleting ${eventType.name} event")
        allEvents.filter {
            it.type == eventType
        }.forEach {
            it.timePast += it.timeLeft
            it.timeLeft = 0
            it.state = RedisTimeEventState.PAST
            redisShortTimeEventRepository.delete(it)
        }
    }

    fun pushEvent(ritualEvent: RedisShortTimeEvent, timeToAdd: Long) {
        ritualEvent.timePast += timeToAdd
        ritualEvent.timeLeft -= timeToAdd
        redisShortTimeEventRepository.save(ritualEvent)
    }
}