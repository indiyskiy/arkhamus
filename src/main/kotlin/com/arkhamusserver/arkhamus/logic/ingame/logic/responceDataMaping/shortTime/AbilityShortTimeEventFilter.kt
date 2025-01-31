package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import org.springframework.stereotype.Component

@Component
class AbilityShortTimeEventFilter() : SpecificShortTimeEventFilter {

    companion object {
        private val shortTimeEventType = ShortTimeEventType.ABILITY_CAST
    }

    override fun accept(event: InGameShortTimeEvent): Boolean =
        event.type == shortTimeEventType


    override fun canSee(
        event: InGameShortTimeEvent,
        user: InGameGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean =
        true

}