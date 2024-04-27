package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class DayTimeEventProcessor(
    private val timeEventRepository: RedisTimeEventRepository,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.DAY

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {

    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {

    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        startTheNight(event, currentGameTime)
    }

    private fun startTheNight(
        event: RedisTimeEvent,
        currentGameTime: Long
    ) {
        val night = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = event.gameId,
            sourceUserId = null,
            targetUserId = null,
            timeStart = currentGameTime,
            timeLeft = RedisTimeEventType.NIGHT.getDefaultTime(),
            timePast = 0L,
            type = RedisTimeEventType.NIGHT,
            state = RedisTimeEventState.ACTIVE,
            xLocation = null,
            yLocation = null
        )
        timeEventRepository.save(night)
    }

}