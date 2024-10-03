package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OneTickShortTimeEvent(
    private val shortTimeEventRepository: RedisShortTimeEventRepository,
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(OneTickShortTimeEvent::class.java)
    }

    @Transactional
    fun processShortTimeEvents(
        timeEvents: List<RedisShortTimeEvent>,
    ): List<RedisShortTimeEvent> {
        val redisTimeEvents: List<RedisShortTimeEvent> = timeEvents.mapNotNull { event ->
            val timeAdd = min(event.timeLeft, ArkhamusOneTickLogic.TICK_DELTA)
            if (event.state == RedisTimeEventState.ACTIVE) {
                processActiveEvent(event, timeAdd)
            } else {
                null
            }
        }
        return redisTimeEvents
    }

    private fun processActiveEvent(
        event: RedisShortTimeEvent,
        timeAdd: Long,
    ): RedisShortTimeEvent {
        event.timePast += timeAdd
        event.timeLeft -= timeAdd
        if (event.timeLeft > 0) {
            shortTimeEventRepository.save(event)
        } else {
            event.state = RedisTimeEventState.PAST
            shortTimeEventRepository.delete(event)
        }
        return event
    }

}