package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisGameUser")
data class RedisGameUser(
    @Id var id: String? = null,
    @Indexed var userId: Long? = null,
    @Indexed var gameId: Long? = null,
    var nickName: String? = null,
    var x: Double? = null,
    var y: Double? = null,
)