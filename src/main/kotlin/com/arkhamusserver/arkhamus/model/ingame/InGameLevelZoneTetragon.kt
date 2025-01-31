package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity

data class InGameLevelZoneTetragon(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var inGameTetragonId: Long,

    var point0X: Double, var point0Y: Double, var point0Z: Double,
    var point1X: Double, var point1Y: Double, var point1Z: Double,
    var point2X: Double, var point2Y: Double, var point2Z: Double,
    var point3X: Double, var point3Y: Double, var point3Z: Double,
) : InGameEntity