package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ActionProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge.ActionMergeHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.ActionRequestMessage
import org.springframework.stereotype.Component

@Component
class ActionCacheHandler(
    val actionMergeHandlers: List<ActionMergeHandler>
) {
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

            val requestType = request.type
            val latestExecutedActionType = requestContainer.lastExecutedAction.requestType

            return requestActionId <= latestExecutedAction && requestType == latestExecutedActionType
        }
        return false
    }

    fun updateCurrentGameDataWithOldAction(requestContainer: NettyTickRequestMessageDataHolder) {
        val actionProcessData = requestContainer.requestProcessData as ActionProcessData
        actionProcessData.updateExecutedSuccessfully(requestContainer.lastExecutedAction.executedSuccessfully)
        mergeRequestProcessData(
            requestContainer.lastExecutedAction.requestType,
            requestContainer.requestProcessData,
            requestContainer.lastExecutedAction.requestProcessData
        )
    }

    fun applyNewestAction(requestContainer: NettyTickRequestMessageDataHolder) {
        val newestAction = getAction(requestContainer)
        val oldAction = requestContainer.lastExecutedAction
        val requestProcessData = requestContainer.requestProcessData
        val actionProcessData = requestProcessData as ActionProcessData
        oldAction.executedSuccessfully = actionProcessData.executedSuccessfully()
        oldAction.actionId = newestAction.actionId()
        oldAction.requestType = requestContainer.nettyRequestMessage.type
        oldAction.requestProcessData = requestProcessData
    }

    private fun mergeRequestProcessData(
        type: String,
        newRequestProcessData: RequestProcessData?,
        cachedRequestProcessData: RequestProcessData?
    ) {
        if (cachedRequestProcessData == null) {
            return
        }
        if (newRequestProcessData == null) {
            return
        }
        if (newRequestProcessData is GameUserData) {
            if (cachedRequestProcessData is GameUserData) {
                actionMergeHandlers
                    .firstOrNull {
                        it.accepts(type)
                    }?.merge(
                        newRequestProcessData,
                        cachedRequestProcessData
                    )
            }
        }

    }

}
