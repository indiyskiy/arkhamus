package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.redis.core.RedisHash
import com.arkhamusserver.arkhamus.model.enums.GameState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisGame")
data class RedisGame(
    @Id var id: String,
    @Indexed var gameId: Long?,
    var currentTick: Long = 0,
    var globalTimer: Long = 0,
    var gameStart: Long = System.currentTimeMillis(),
    var godTimer: Long = 0,
    @Indexed var state: String = GameState.IN_PROGRESS.name
)