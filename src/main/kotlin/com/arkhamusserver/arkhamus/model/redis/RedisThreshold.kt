package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisThreshold")
data class RedisThreshold(
    @Id var id: String,
    @Indexed var gameId: Long,
    var thresholdId: Long,

    var x: Double = 0.0,
    var y: Double = 0.0,
    var zoneId: Long,

) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}