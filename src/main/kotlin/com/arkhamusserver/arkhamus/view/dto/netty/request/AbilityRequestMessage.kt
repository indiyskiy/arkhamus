package com.arkhamusserver.arkhamus.view.dto.netty.request

class AbilityRequestMessage(
    var abilityId: Int,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)