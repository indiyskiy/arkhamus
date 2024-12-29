package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class InventoryCellRequest(
    var itemId: Int = Item.PURE_NOTHING.id,
    var number: Int = 0
) {
    constructor(
        inventoryCell: InventoryCell
    ) : this(
        inventoryCell.item.id,
        inventoryCell.number
    )
}