package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisCraftProcess")
data class RedisCraftProcess(
    @Id var id: String,
    @Indexed var gameId: Long,
    @Indexed var recipeId: Int,
    @Indexed var sourceUserId: Long,
    @Indexed var targetCrafterId: Long,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
)