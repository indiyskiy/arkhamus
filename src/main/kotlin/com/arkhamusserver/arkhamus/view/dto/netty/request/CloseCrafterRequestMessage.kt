package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell

class CloseCrafterRequestMessage(
    var crafterId: Long,
    var newInventoryContent: List<ContainerCell>,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)