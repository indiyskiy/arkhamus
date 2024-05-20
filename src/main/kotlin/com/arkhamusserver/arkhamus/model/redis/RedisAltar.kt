package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltar")
data class RedisAltar(
    @Id var id: String,
    @Indexed var altarId: Long,
    @Indexed var gameId: Long,
    @Indexed var state: MapAltarState = MapAltarState.OPEN,
    var x: Double,
    var y: Double,
    var interactionRadius: Double,
)