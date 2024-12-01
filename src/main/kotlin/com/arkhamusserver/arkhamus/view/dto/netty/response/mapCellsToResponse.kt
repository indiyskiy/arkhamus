package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCellResponse

fun List<InventoryCell>.mapCellsToResponse(): List<InventoryCellResponse> =
    this.map { InventoryCellResponse(it) }