package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAbilityCast")
data class RedisAbilityCast(
    @Id var id: String,
    @Indexed var gameId: Long,
    var abilityId: Int,
    var sourceUserId: Long? = null,
    var targetUserId: Long? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeftCooldown: Long,
    var timeLeftActive: Long,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
    @TimeToLive val timeToLive: Long = 7200
)