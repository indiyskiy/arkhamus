package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer

data class ContainerStateResponse(
    var containerId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?,
    var gameTags: Set<String>
) {
    constructor(container: RedisContainer) : this(
        containerId = container.inGameId(),
        state = container.state,
        holdingUserId = container.holdingUser,
        gameTags = container.gameTags().map { it.name }.toSet()
    )
}