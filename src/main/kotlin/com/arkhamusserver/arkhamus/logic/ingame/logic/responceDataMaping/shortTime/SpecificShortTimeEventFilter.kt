package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent

interface SpecificShortTimeEventFilter {
    fun accept(
        event: InGameShortTimeEvent,
    ): Boolean

    fun canSee(
        event: InGameShortTimeEvent,
        user: InGameGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean
}