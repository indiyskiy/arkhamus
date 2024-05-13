package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.view.dto.netty.response.InventoryCell

class CraftProcessRequestMessage(
    var recipeId: Int,
    var newInventoryContent: List<InventoryCell>,
    var crafterId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)