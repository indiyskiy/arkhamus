package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent

data class GlobalGameData(
    val game: RedisGame,
    var users: Map<Long, RedisGameUser> = emptyMap(),
    var containers: Map<Long, RedisContainer> = emptyMap(),
    var timeEvents: List<RedisTimeEvent> = emptyList()
)