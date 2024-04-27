package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAbilityCast")
data class RedisAbilityCast(
    @Id var id: String,
    @Indexed var gameId: Long,
    @Indexed var abilityId: Int,
    @Indexed var sourceUserId: Long? = null,
    @Indexed var targetUserId: Long? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
)