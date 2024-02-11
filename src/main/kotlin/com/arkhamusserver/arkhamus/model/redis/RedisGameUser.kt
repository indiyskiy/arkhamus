package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisGameUser")
data class RedisGameUser(
    @Id var id: String,
    @Indexed var userId: Long,
    var nickName: String,
    @Indexed var gameId: Long,
    var x: Double = 0.0,
    var y: Double = 0.0,
)