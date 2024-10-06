package com.arkhamusserver.arkhamus.view.dto.netty.request.lantern

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class FillLanternRequestMessage(
    var lanternId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)