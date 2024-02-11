package com.arkhamusserver.arkhamus.view.dto.netty.request

open class NettyBaseRequestMessage(
    var baseRequestData: BaseRequestData,
    type: String,
) : NettyRequestMessage(type) {
}