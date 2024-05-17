package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.view.dto.netty.request.ActionRequestMessage
import org.springframework.stereotype.Component

@Component
class ActionCacheHandler {
    fun isAction(requestContainer: NettyTickRequestMessageDataHolder): Boolean {
        return requestContainer.nettyRequestMessage is ActionRequestMessage
    }
    fun getAction(requestContainer: NettyTickRequestMessageDataHolder): ActionRequestMessage {
        return requestContainer.nettyRequestMessage as ActionRequestMessage
    }

    fun isOldAction(requestContainer: NettyTickRequestMessageDataHolder): Boolean {
        val request = requestContainer.nettyRequestMessage
        if (request is ActionRequestMessage) {
            val requestActionId = request.actionId()
            val latestExecutedAction = requestContainer.lastExecutedAction.actionId
            return requestActionId <= latestExecutedAction
        }
        return false
    }

}
