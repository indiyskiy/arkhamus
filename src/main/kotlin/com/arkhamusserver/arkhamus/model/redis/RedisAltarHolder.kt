package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltarHolder")
data class RedisAltarHolder(
    @Id var id: String,
    @Indexed var gameId: Long,

    var lockedGodId: Int? = null,
    var itemsForRitual: Map<Int, Int> = emptyMap(),
    var itemsIdToAltarId: Map<Int, Long> = emptyMap(),
    var itemsOnAltars: Map<Int, Int> = emptyMap(),

    var state: MapAltarState = MapAltarState.OPEN,
)