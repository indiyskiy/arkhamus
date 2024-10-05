package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer

data class ContainerState(
    var containerId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?,
    var gameTags: List<InGameObjectTag>
) {
    constructor(container: RedisContainer) : this(
        containerId = container.inGameId(),
        state = container.state,
        holdingUserId = container.holdingUser,
        gameTags = container.gameTags()
    )
}