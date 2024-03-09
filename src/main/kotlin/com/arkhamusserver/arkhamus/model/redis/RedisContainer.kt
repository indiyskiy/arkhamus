package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisContainer")
data class RedisContainer(
    @Id var id: String,
    @Indexed var containerId: Long,
    @Indexed var gameId: Long,
    @Indexed var holdingUser: Long? = null,
    @Indexed var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var interactionRadius: Double = 0.0,
    var items: MutableMap<Long, Long> = HashMap()
)