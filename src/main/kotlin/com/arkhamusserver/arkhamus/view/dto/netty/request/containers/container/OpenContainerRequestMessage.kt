package com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class OpenContainerRequestMessage(
    var externalInventoryId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)