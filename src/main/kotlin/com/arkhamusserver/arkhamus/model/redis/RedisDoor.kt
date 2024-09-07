package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.DoorState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisDoor")
data class RedisDoor(
    @Id var id: String,
    @Indexed var gameId: Long,
    var doorId: Long,

    var x: Double,
    var y: Double,
    var z: Double,
    var zoneId: Long,
    var globalState: DoorState = DoorState.OPEN,
    var closedForUsers: MutableList<Long> = mutableListOf(),

    )  : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }
}