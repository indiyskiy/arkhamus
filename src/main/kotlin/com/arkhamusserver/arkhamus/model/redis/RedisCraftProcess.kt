package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState

data class RedisCraftProcess(
    override var id: String,
    override var gameId: Long,
    var recipeId: Int,
    var targetCrafterId: Long,
    var sourceUserId: Long,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
) : RedisGameEntity