package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisTimeEvent(
    override var id: String,
    override var gameId: Long,
    var sourceObjectId: Long? = null,
    var targetObjectId: Long? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var type: RedisTimeEventType,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,
    var visibilityModifiers: Set<VisibilityModifier>,
) : RedisGameEntity, WithVisibilityModifiers {
    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}