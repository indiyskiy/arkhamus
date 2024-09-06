package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.DoorUserState

data class DoorResponse(
    var doorId: Long,
    var doorState: DoorUserState,
)
