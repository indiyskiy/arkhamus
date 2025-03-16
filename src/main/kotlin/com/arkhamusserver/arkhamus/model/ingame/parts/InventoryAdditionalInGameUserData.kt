package com.arkhamusserver.arkhamus.model.ingame.parts

import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

data class InventoryAdditionalInGameUserData(
    var items: List<InventoryCell> = emptyList(),
    var maxItems: Int,
)