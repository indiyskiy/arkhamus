package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType

data class LevelZoneResponse(
    val zoneId: Long,
    val zoneType: ZoneType
) {
    constructor(levelZone: LevelZone) : this(levelZone.zoneId, levelZone.zoneType)
}