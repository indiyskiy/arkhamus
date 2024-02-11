package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.GetContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyRequestHandler : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == GetContainerRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData
    ): GameResponseMessage {
        val userId = nettyTickRequestMessageContainer.userAccount.id
        val request = nettyTickRequestMessageContainer.nettyRequestMessage
        with(request as GetContainerRequestMessage) {
            nettyTickRequestMessageContainer.gameSession?.id?.let { gameId ->
                val container = globalGameData.containers[this.containerId]!!
                val user = globalGameData.users[userId]!!
                val users = globalGameData.users.values.filter { it.userId != userId }
                return ContainerGameResponse(container, user, users)
            } ?: return ErrorGameResponse("game session id is null")
        }
    }
}