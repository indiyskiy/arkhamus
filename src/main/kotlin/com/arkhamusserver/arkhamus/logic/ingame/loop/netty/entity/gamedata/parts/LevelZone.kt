package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType

data class LevelZone(
    val zoneId: Long,
    val zoneType: ZoneType
)