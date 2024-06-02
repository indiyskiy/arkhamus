package com.arkhamusserver.arkhamus.view.dto.netty.request

class AltarOpenRequestMessage(
    var altarId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)