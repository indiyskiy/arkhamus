package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser

interface SpecificShortTimeEventFilter {
    fun accept(
        event: InGameShortTimeEvent,
    ): Boolean

    fun canSee(
        event: InGameShortTimeEvent,
        user: InGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean
}