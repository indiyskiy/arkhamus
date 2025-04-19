package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
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
        user: InGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean =
        true

}