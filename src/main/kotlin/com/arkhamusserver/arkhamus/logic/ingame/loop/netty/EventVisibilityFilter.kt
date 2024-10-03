package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class EventVisibilityFilter {
    fun filter(user: RedisGameUser, ongoingEvents: List<OngoingEvent>): List<OngoingEvent> =
        ongoingEvents.filter {
            when (it.event.type.getVisibility()) {
                PUBLIC -> true
                CULTIST_VISIBILITY -> user.role == CULTIST
                NONE -> false
                SOURCE -> it.event.sourceObjectId == user.userId
                TARGET -> it.event.targetObjectId == user.userId
                SOURCE_AND_TARGET -> it.event.sourceObjectId == user.userId || it.event.targetObjectId == user.userId
            }
        }

}