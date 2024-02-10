package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.redis.core.RedisHash

@RedisHash("RedisContainer")
data class RedisGameUser(
    var id: String? = null,
    var x: Double? = null,
    var y: Double? = null,
)