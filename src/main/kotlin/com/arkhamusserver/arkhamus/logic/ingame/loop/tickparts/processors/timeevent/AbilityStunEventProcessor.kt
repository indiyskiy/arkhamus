package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component

@Component
class AbilityStunEventProcessor() : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.ABILITY_STUN


    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        val user = globalGameData.users[event.targetObjectId]
        if (user == null) return
        user.stateTags += UserStateTag.STUN
    }

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        val user = globalGameData.users[event.targetObjectId]
        if (user == null) return
        user.stateTags += UserStateTag.STUN
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        val user = globalGameData.users[event.targetObjectId]
        if (user == null) return
        user.stateTags -= UserStateTag.STUN
    }
}