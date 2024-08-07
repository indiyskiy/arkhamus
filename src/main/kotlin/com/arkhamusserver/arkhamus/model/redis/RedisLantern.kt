package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLantern")
data class RedisLantern(
    @Id var id: String,
    @Indexed var gameId: Long,
    var lanternId: Long,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var filled: Boolean = false,
    var activated: Boolean = false,
    var x: Double,
    var y: Double,
    var lightRange: Double,
    @TimeToLive val timeToLive: Long = 10800
) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}