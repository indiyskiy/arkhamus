package com.arkhamusserver.arkhamus.view.dto.netty.request

class OpenContainerRequestMessage(
    var externalInventoryId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)