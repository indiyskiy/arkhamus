package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.redis.core.RedisHash
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisContainer")
data class RedisContainer(
    @Id var id: String,
    @Indexed var containerId: Long,
    @Indexed var gameId: Long,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var interactionRadius: Double = 0.0,
    var items: Map<String, Long> = HashMap()
)