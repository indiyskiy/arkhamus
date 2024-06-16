package com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class OpenCrafterRequestMessage(
    var externalInventoryId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)