package com.arkhamusserver.arkhamus.view.dto.netty.request

class ISawTheEndOfTimesRequestMessage(
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)