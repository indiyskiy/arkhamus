package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltarHolder")
data class RedisAltarHolder(
    @Id var id: String,
    @Indexed var gameId: Long,
    var state: MapAltarState = MapAltarState.OPEN,
)