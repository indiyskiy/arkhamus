package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class AltarFailedEventProcessor(
    private val ritualHandler: RitualHandler,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.ALTAR_VOTING_COOLDOWN

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
        globalGameData.altarPolling?.let {
            ritualHandler.finishAltarPolling(it, globalGameData)
        }
    }


}