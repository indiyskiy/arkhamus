package com.arkhamusserver.arkhamus.view.dto.netty.request

class GameEndedRequestMessage(
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)