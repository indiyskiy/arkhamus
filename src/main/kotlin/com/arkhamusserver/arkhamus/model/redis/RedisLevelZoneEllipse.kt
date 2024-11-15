package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity

data class RedisLevelZoneEllipse(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var inGameTetragonId: Long,

    var pointX: Double, var pointY: Double, var pointZ: Double,
    var height: Double, var width: Double,
) : RedisGameEntity