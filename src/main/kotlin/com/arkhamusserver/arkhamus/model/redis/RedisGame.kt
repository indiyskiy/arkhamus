package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.GameState
import org.springframework.data.redis.core.RedisHash

@RedisHash("RedisGame")
data class RedisGame(
    var id: String,
    var currentTick: Long = 0,
    var globalTimer: Long = 0,
    var gameStart: Long = System.currentTimeMillis(),
    var godTimer: Long = 0,
    var state: String = GameState.IN_PROGRESS.name
)