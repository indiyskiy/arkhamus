package com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter

import com.arkhamusserver.arkhamus.view.dto.netty.request.ActionRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.ExternalInventoryRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCellRequest

class CraftProcessRequestMessage(
    var recipeId: Int,
    var actionId: Long,
    externalInventoryId: Long,
    newInventoryContent: List<InventoryCell>,
    type: String,
    baseRequestData: BaseRequestData
) : ExternalInventoryRequestMessage(
    externalInventoryId = externalInventoryId,
    newInventoryContent = newInventoryContent.map { InventoryCellRequest(it) },
    close = false,
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