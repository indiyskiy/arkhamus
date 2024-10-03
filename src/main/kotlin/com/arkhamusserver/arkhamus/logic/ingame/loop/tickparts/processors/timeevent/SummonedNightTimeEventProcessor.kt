package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class SummonedNightTimeEventProcessor(
    private val userLocationHandler: UserLocationHandler,
    private val userMadnessHandler: UserMadnessHandler,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.SUMMONED_NIGHT

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
        if (isCommonNight(globalGameData)) {
            return
        }
        globalGameData.users.filter {
            userLocationHandler.isInDarkness(it.value, globalGameData)
        }.forEach {
            userMadnessHandler.applyNightMadness(it.value)
        }
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        event.state = RedisTimeEventState.PAST
    }

    private fun isCommonNight(globalGameData: GlobalGameData) =
        globalGameData.timeEvents.any {
            it.state == RedisTimeEventState.ACTIVE &&
                    it.type == RedisTimeEventType.NIGHT
        }
}