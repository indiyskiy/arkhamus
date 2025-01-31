package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer

data class ContainerStateResponse(
    var containerId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?,
    var gameTags: Set<String>
) {
    constructor(container: InGameContainer) : this(
        containerId = container.inGameId(),
        state = container.state,
        holdingUserId = container.holdingUser,
        gameTags = container.gameTags().map { it.name }.toSet()
    )
}