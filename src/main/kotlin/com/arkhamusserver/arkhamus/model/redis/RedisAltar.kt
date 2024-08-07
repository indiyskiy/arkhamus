package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltar")
data class RedisAltar(
    @Id var id: String,
    var altarId: Long,
    @Indexed var gameId: Long,
    var x: Double,
    var y: Double,
    var interactionRadius: Double,
    @TimeToLive val timeToLive: Long = 7200
) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}