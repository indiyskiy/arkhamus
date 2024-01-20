package com.arkhamusserver.arkhamus.view.dto.netty.request

data class GetContainerRequestMessage(
    var containerId: Long,
    var type: String
) : NettyRequestMessage