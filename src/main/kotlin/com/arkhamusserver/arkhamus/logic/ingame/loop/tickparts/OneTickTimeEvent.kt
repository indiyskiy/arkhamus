package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.TimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class OneTickTimeEvent(
    private val timeEventRepository: RedisTimeEventRepository,
    private val timeEventProcessors: List<TimeEventProcessor>
) {

    companion object{
        var logger: Logger = LoggerFactory.getLogger(OneTickTimeEvent::class.java)
    }
    fun processTimeEvents(
        game: RedisGame,
        timeEvents: List<RedisTimeEvent>,
        currentGameTime: Long
    ): List<OngoingEvent> {
        val redisTimeEvents: List<OngoingEvent> = timeEvents.mapNotNull { event ->
            val timeAdd = min(event.timeLeft, ArkhamusOneTickLogic.TICK_DELTA)
            if (event.state == RedisTimeEventState.ACTIVE) {
                processActiveEvent(event, timeAdd, currentGameTime)
            } else {
                null
            }
        }
        return redisTimeEvents
    }

    private fun processActiveEvent(
        event: RedisTimeEvent,
        timeAdd: Long,
        currentGameTime: Long
    ): OngoingEvent {
        val changedEvent = process(event, timeAdd, currentGameTime)
        return changedEvent
    }

    private fun process(
        event: RedisTimeEvent,
        timeAdd: Long,
        currentGameTime: Long
    ): OngoingEvent {
        if (event.timePast == 0L) {
            applyStartProcessors(event, currentGameTime)
        }
        event.timePast += timeAdd
        event.timeLeft -= timeAdd
        if (event.timeLeft > 0) {
            logger.info("process ${event.type}")
            applyProcessors(event, currentGameTime)
            timeEventRepository.save(event)
        } else {
            logger.info("end ${event.type}")
            applyEndProcessors(event, currentGameTime)
            timeEventRepository.delete(event)
        }
        return OngoingEvent(
            event = event,
        )
    }

    private fun applyStartProcessors(event: RedisTimeEvent, currentGameTime: Long) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.processStart(event, currentGameTime)
            }
    }

    private fun applyEndProcessors(event: RedisTimeEvent, currentGameTime: Long) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.processEnd(event, currentGameTime)
            }
    }

    private fun applyProcessors(event: RedisTimeEvent, currentGameTime: Long) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.process(event, currentGameTime)
            }
    }
}