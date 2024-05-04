package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.NettyResponseBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class OneTickUserResponses(
    private val nettyResponseBuilder: NettyResponseBuilder,
) {
    fun buildResponses(
        currentTick: Long,
        globalGameData: GlobalGameData,
        currentTasks: List<NettyTickRequestMessageDataHolder>,
    ): List<NettyResponseMessage> {
        val tasksUniqueByUserAndTick = currentTasks.distinctBy { it.userAccount.id to it.nettyRequestMessage.baseRequestData.tick }

        return tasksUniqueByUserAndTick.map{ task -> buildResponse(task, globalGameData) }
    }

    private fun buildResponse(
        request: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
    ): NettyResponseMessage {
        return nettyResponseBuilder.buildResponse(request, globalGameData)
    }
}