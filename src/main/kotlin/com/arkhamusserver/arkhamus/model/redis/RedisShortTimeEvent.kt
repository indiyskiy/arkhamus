package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

data class RedisShortTimeEvent(
    override var id: String,
    override var gameId: Long,

    var sourceId: Long? = null,

    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,

    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,

    var type: ShortTimeEventType,
    var state: RedisTimeEventState,

    var visibilityModifiers: MutableSet<String>,
) : RedisGameEntity, WithVisibilityModifiers {
    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }
}