package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

data class RedisLevelZoneEllipse(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var inGameTetragonId: Long,

    var pointX: Double, var pointY: Double, var pointZ: Double,
    var height: Double, var width: Double,
): RedisGameEntity