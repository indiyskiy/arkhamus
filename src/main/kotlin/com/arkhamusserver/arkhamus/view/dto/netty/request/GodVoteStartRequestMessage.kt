package com.arkhamusserver.arkhamus.view.dto.netty.request

class GodVoteStartRequestMessage(
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