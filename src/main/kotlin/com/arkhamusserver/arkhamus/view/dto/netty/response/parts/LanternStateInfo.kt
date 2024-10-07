package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState

data class LanternStateInfo(
    var costValue: Int = 1,
    var costItem: Int = Item.SOLARITE.id,
    var state: LanternState,
    var fuel: Double,
)