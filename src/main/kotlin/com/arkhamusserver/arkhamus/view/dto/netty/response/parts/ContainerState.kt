package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer

data class ContainerState(
    var containerId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?
) {
    constructor(container: RedisContainer) : this(
        containerId = container.containerId,
        state = container.state,
        holdingUserId = container.holdingUser
    )
}