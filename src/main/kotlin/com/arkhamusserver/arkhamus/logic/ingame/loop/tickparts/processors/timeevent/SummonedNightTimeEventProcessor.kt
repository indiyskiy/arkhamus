package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component

@Component
class SummonedNightTimeEventProcessor(
    private val userLocationHandler: UserLocationHandler,
    private val userMadnessHandler: UserMadnessHandler,
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.SUMMONED_NIGHT

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        if (isCommonNight(globalGameData)) {
            return
        }
        globalGameData.users.filter {
            userLocationHandler.isInDarkness(it.value, globalGameData)
        }.forEach {
            userMadnessHandler.applyNightMadness(it.value, timePassedMillis, currentGameTime, globalGameData)
        }
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        event.state = InGameTimeEventState.PAST
    }

    private fun isCommonNight(globalGameData: GlobalGameData) =
        globalGameData.timeEvents.any {
            it.state == InGameTimeEventState.ACTIVE &&
                    it.type == InGameTimeEventType.NIGHT
        }
}