package com.arkhamusserver.arkhamus.view.dto.netty.request.containers

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

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