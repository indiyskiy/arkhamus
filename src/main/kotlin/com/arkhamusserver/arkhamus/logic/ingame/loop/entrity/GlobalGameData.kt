package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.redis.*

data class GlobalGameData(
    val game: RedisGame,
    var altarHolder: RedisAltarHolder,
    var altarPolling: RedisAltarPolling? = null,
    var altars: Map<Long, RedisAltar> = emptyMap(),
    var users: Map<Long, RedisGameUser> = emptyMap(),
    var containers: Map<Long, RedisContainer> = emptyMap(),
    var crafters: Map<Long, RedisCrafter> = emptyMap(),
    var lanterns: Map<Long, RedisLantern> = emptyMap(),
    var timeEvents: List<RedisTimeEvent> = emptyList(),
    var castedAbilities: List<RedisAbilityCast> = emptyList(),
    var craftProcess: List<RedisCraftProcess> = emptyList(),
    var inBetweenEvents: InBetweenEventHolder = InBetweenEventHolder()
)