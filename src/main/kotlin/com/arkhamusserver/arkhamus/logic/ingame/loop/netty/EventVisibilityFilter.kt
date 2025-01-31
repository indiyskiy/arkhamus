package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility.*
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.springframework.stereotype.Component

@Component
class EventVisibilityFilter {
    fun filter(user: InGameGameUser, ongoingEvents: List<OngoingEvent>): List<OngoingEvent> =
        ongoingEvents.filter {
            when (it.event.type.getVisibility()) {
                PUBLIC -> true
                NONE -> false
                SOURCE -> isSource(it, user)
                TARGET -> isTarget(it, user)
                SOURCE_AND_TARGET -> isSource(it, user) || isTarget(it, user)
            }
        }

    private fun isTarget(
        event: OngoingEvent,
        user: InGameGameUser
    ): Boolean = event.event.targetObjectId == user.inGameId()

    private fun isSource(
        event: OngoingEvent,
        user: InGameGameUser
    ): Boolean = event.event.sourceObjectId == user.inGameId()

}