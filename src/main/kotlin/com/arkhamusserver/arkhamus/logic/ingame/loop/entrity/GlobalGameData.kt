package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.redis.*

data class GlobalGameData(
    val game: RedisGame,
    var users: Map<Long, RedisGameUser> = emptyMap(),
    var containers: Map<Long, RedisContainer> = emptyMap(),
    var lanterns: Map<Long, RedisLantern> = emptyMap(),
    var timeEvents: List<RedisTimeEvent> = emptyList(),
    var castedAbilities: List<RedisAbilityCast> = emptyList()
)