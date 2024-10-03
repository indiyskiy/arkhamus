package com.arkhamusserver.arkhamus.view.dto.netty.request

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType

class AbilityRequestMessage(
    var abilityId: Int,
    val targetId: String?,
    val targetType: GameObjectType?,
    var actionId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type), ActionRequestMessage {

    override fun actionId(): Long {
        return actionId
    }

    override fun updateActionId(actionId: Long) {
        this.actionId = actionId
    }
}