package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class EventVisibilityFilter {
    fun filter(user: RedisGameUser, ongoingEvents: List<OngoingEvent>): List<OngoingEvent> =
        ongoingEvents.filter {
            when (it.event.type.getVisibility()) {
                Visibility.PUBLIC -> true
                Visibility.CULTIST -> user.role == RoleTypeInGame.CULTIST
                Visibility.NONE -> false
                Visibility.SOURCE -> it.event.sourceUserId == user.userId
                Visibility.TARGET -> it.event.targetUserId == user.userId
                Visibility.SOURCE_AND_TARGET -> it.event.sourceUserId == user.userId || it.event.targetUserId == user.userId
            }
        }

}