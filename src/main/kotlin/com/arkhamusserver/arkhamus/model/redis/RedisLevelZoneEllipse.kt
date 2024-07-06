package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLevelZoneEllipse")
data class RedisLevelZoneEllipse(
    @Id
    var id: String,
    @Indexed
    var gameId: Long,
    @Indexed
    var levelZoneId: Long,
    var inGameTetragonId: Long,

    var pointX: Double, var pointY: Double,
    var height: Double, var width: Double,
)