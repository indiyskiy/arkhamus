package com.arkhamusserver.arkhamus.view.dto.netty.request.ritual

import com.arkhamusserver.arkhamus.view.dto.netty.request.ActionRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class GodVoteCastRequestMessage(
    var godId: Long,
    var altarId: Long,
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