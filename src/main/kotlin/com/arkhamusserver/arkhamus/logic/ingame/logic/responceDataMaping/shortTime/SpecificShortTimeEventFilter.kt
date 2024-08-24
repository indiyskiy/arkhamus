package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent

interface SpecificShortTimeEventFilter {
    fun filter(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<RedisShortTimeEvent>
}