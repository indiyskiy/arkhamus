package com.arkhamusserver.arkhamus.view.dto.netty.request

data class GetContainerRequestMessage(
    var containerId: Long,
    var type: String,
    var baseRequestData: BaseRequestData
) : NettyTickRequestMessage {

    override fun tick(): Long =
        baseRequestData.tick
}