package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import org.springframework.stereotype.Component

@Component
class PeecabooShortTimeEventFilter() : SpecificShortTimeEventFilter {

    companion object {
        private val shortTimeEventTypes = setOf(
            ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CONTAINER,
            ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CRAFTER,
        )
    }

    override fun accept(event: InGameShortTimeEvent): Boolean =
        event.type in shortTimeEventTypes


    override fun canSee(
        event: InGameShortTimeEvent,
        user: InGameGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean =
        true

}