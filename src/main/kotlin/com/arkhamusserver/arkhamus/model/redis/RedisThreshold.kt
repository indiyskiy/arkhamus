package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId

data class RedisThreshold(
    override var id: String,
    override var gameId: Long,
    var thresholdId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var zoneId: Long,
    var type: ThresholdType,
) : RedisGameEntity, WithPoint, WithTrueIngameId {

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