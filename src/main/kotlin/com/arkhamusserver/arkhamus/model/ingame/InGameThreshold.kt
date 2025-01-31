package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId

data class InGameThreshold(
    override var id: String,
    override var gameId: Long,
    var thresholdId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var zoneId: Long,
    var type: ThresholdType,
) : InGameEntity, WithPoint, WithTrueIngameId {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun inGameId(): Long {
        return thresholdId
    }
}