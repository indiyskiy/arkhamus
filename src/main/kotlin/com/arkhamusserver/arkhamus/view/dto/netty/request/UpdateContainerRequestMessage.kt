package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell

class UpdateContainerRequestMessage(
    var containerId: Long,
    var newInventoryContent: List<ContainerCell>,
    var close: Boolean,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)