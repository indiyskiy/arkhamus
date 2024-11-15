package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class DayTimeEventProcessor(
    private val timeEventHandler: TimeEventHandler,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.DAY

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        startTheNight(event, currentGameTime)
    }

    private fun startTheNight(
        event: RedisTimeEvent,
        currentGameTime: Long
    ) {
        timeEventHandler.createEvent(
            event.gameId,
            RedisTimeEventType.NIGHT,
            currentGameTime,
            sourceObject = null
        )
    }

}