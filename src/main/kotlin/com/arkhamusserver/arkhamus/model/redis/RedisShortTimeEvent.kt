package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisShortTimeEvent(
    override var id: String,
    override var gameId: Long,

    var sourceId: Long? = null,
    var objectId: Long? = null,

    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,

    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,

    var type: ShortTimeEventType,
    var state: RedisTimeEventState,

    var visibilityModifiers: MutableSet<VisibilityModifier>,
    var additionalData: Any? = null,
) : RedisGameEntity, WithVisibilityModifiers {
    override fun visibilityModifiers(): MutableSet<VisibilityModifier> {
        return visibilityModifiers
    }
}