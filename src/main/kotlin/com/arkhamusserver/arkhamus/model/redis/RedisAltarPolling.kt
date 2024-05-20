package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltar")
data class RedisAltarPolling(
    @Id var id: String,
    @Indexed var altarId: Long,
    @Indexed var gameId: Long,
    @Indexed var startedUserId: Long,
    @Indexed var userVotes: MutableMap<Long, Long> = HashMap(),
    @Indexed var state: MapAltarPollingState = MapAltarPollingState.ONGOING,
)