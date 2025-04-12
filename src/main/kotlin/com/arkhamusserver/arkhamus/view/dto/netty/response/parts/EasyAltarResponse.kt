package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState

data class EasyAltarResponse(
    var altarId: Long,
    var state: MapObjectState,
    var altarState: MapAltarState
)