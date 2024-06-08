package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltarPolling")
data class RedisAltarPolling(
    @Id var id: String,
    @Indexed var gameId: Long,
    @Indexed var state: MapAltarPollingState = MapAltarPollingState.ONGOING,
    var altarId: Long,
    var startedUserId: Long,
    var userVotes: MutableMap<Long, Long> = HashMap(),
    var skippedUsers: List<Long> = ArrayList(),
    var started: Long,
)