package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class AbilityStunEventProcessor() : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.ABILITY_STUN


    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        globalGameData.users[event.targetObjectId]?.stateTags?.add(UserStateTag.STUN.name)
    }

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        globalGameData.users[event.targetObjectId]?.stateTags?.add(UserStateTag.STUN.name)
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        globalGameData.users[event.targetObjectId]?.stateTags?.remove(UserStateTag.STUN.name)
    }
}