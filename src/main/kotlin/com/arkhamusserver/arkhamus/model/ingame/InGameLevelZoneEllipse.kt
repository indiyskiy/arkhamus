package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity

data class InGameLevelZoneEllipse(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var inGameTetragonId: Long,

    var pointX: Double, var pointY: Double, var pointZ: Double,
    var height: Double, var width: Double,
) : InGameEntity