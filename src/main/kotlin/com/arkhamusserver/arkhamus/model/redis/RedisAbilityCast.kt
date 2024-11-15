package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState

data class RedisAbilityCast(
    override var id: String,
    override var gameId: Long,
    var abilityId: Int,
    var sourceUserId: Long? = null,
    var targetId: String? = null,
    var targetType: GameObjectType ? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeftCooldown: Long,
    var timeLeftActive: Long,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
): RedisGameEntity