package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.VisibilityMap

data class LevelGeometryData(
    var zones: List<GameDataLevelZone> = emptyList(),
    var visibilityMap: VisibilityMap? = null
)