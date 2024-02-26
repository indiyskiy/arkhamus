package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

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

    override fun processStart(event: RedisTimeEvent, currentGameTime: Long) {

    }

    override fun process(event: RedisTimeEvent, currentGameTime: Long) {

    }

    override fun processEnd(event: RedisTimeEvent, currentGameTime: Long) {
        event.state = RedisTimeEventState.PAST
        startTheNight(event)
    }

    private fun startTheNight(event: RedisTimeEvent) {
        val night = RedisTimeEvent(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = event.gameId,
            sourceUserId = null,
            targetUserId = null,
            timeStart = 0L,
            timeLeft = RedisTimeEventType.NIGHT.getDefaultTime(),
            timePast = 0L,
            type = RedisTimeEventType.NIGHT,
            state = RedisTimeEventState.ACTIVE
        )
        timeEventRepository.save(night)
    }

}