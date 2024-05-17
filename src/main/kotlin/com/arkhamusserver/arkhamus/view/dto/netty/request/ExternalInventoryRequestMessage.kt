package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.view.dto.netty.response.InventoryCell

abstract class ExternalInventoryRequestMessage(
    var externalInventoryId: Long,
    var newInventoryContent: List<InventoryCell>,
    var close: Boolean,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(
    baseRequestData = baseRequestData,
    type = type
)