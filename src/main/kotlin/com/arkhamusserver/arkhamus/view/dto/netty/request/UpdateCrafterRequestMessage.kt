package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.view.dto.netty.response.InventoryCell

class UpdateCrafterRequestMessage(
    var crafterId: Long,
    var newInventoryContent: List<InventoryCell>,
    var close: Boolean,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)