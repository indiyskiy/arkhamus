package com.arkhamusserver.arkhamus.view.dto.netty.request

class HeartbeatRequestMessage(
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)