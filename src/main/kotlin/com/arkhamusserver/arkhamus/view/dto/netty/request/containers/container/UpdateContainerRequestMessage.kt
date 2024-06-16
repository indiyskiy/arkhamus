package com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container

import com.arkhamusserver.arkhamus.view.dto.netty.request.ActionRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.ExternalInventoryRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class UpdateContainerRequestMessage(
    var actionId: Long,
    externalInventoryId: Long,
    newInventoryContent: List<InventoryCell>,
    close: Boolean,
    type: String,
    baseRequestData: BaseRequestData
) : ExternalInventoryRequestMessage(
    externalInventoryId = externalInventoryId,
    newInventoryContent = newInventoryContent,
    close = close,
    type = type,
    baseRequestData = baseRequestData
), ActionRequestMessage {
    override fun actionId(): Long {
        return actionId
    }

    override fun updateActionId(actionId: Long) {
        this.actionId = actionId
    }
}