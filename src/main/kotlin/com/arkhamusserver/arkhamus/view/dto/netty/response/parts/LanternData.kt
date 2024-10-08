package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState

data class LanternData(
    var lanternId: Long,
    var lanternState: LanternState,
    var objectState: MapObjectState,
    var lightRange: Double,
)