package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toItem

data class InventoryCell(
    var item: Item = Item.PURE_NOTHING,
    var number: Int = 0
) {
    constructor(request: InventoryCellRequest) : this(
        request.itemId.toItem() ?: Item.PURE_NOTHING,
        request.number
    )
}