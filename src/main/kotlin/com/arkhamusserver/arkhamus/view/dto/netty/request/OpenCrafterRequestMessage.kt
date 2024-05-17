package com.arkhamusserver.arkhamus.view.dto.netty.request

class OpenCrafterRequestMessage(
    var externalInventoryId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)