package com.arkhamusserver.arkhamus.view.dto.netty.request.containers

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCellRequest

abstract class ExternalInventoryRequestMessage(
    var externalInventoryId: Long,
    var newInventoryContent: List<InventoryCellRequest>,
    var close: Boolean,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(
    baseRequestData = baseRequestData,
    type = type
)