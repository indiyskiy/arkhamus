package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState

data class ContainerState (
    var containerId: Long,
    var state: MapObjectState,
    var holdingUserId: Long?
)