package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.redis.core.RedisHash
import org.springframework.data.annotation.Id
@RedisHash("RedisContainer")
data class RedisContainer(
   @Id var id: String? = null,
    var x: Double? = null,
    var y: Double? = null,
    var interactionRadius: Double? = null,
    var items: Map<String, Long> = HashMap()
)