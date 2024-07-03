package com.arkhamusserver.arkhamus.view.dto.netty.request

abstract class NettyBaseRequestMessage(
    var baseRequestData: BaseRequestData,
    type: String,
) : NettyRequestMessage(type)