package com.arkhamusserver.arkhamus.view.dto.netty.request

class GetContainerRequestMessage(
    var containerId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)