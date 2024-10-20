package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType

data class GameDataLevelZone(
    val zoneId: Long,
    val zoneType: ZoneType,
    val tetragons: List<GeometryUtils.Tetragon>,
    val ellipses: List<GeometryUtils.Ellipse>
)