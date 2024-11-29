package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class InventoryCell(
    var item: Item = Item.PURE_NOTHING,
    var number: Int = 0
)