package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisGame")
data class RedisGame(
    @Id var id: String,
    @Indexed var gameId: Long?,
    var god: God,
    var currentTick: Long = -1,
    var lastTickSaveHeartbeatActivity: Long = 0,
    var globalTimer: Long = 0,
    var serverTimeLastTick: Long = 0,
    var serverTimeCurrentTick: Long = 0,
    var lastTimeSentResponse: Long = 0,
    var gameStart: Long = System.currentTimeMillis(),
    @Indexed var state: String = GameState.PENDING.name,
    var gameEndReason: String? = null,
) : WithTrueIngameId {
    override fun inGameId(): Long {
        return gameId!!
    }
}