package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLantern")
data class RedisLantern(
    @Id var id: String,
    @Indexed var lanternId: Long,
    @Indexed var gameId: Long,
    @Indexed var state: MapObjectState = MapObjectState.ACTIVE,
    @Indexed var filled: Boolean = false,
    @Indexed var activated: Boolean = false,
    var x: Double,
    var y: Double,
    var lightRange: Double,
)