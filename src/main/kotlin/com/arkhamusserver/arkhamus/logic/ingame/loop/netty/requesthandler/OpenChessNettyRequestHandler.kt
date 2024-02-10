package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.view.dto.netty.request.GetContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenChessNettyRequestHandler(
    private val containerRepository: ContainerRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == GetContainerRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ): GameResponseMessage {
        val request = nettyTickRequestMessageContainer.nettyRequestMessage
        with(request as GetContainerRequestMessage) {
            val container =
                nettyTickRequestMessageContainer.gameSession?.id?.let {
                    containerRepository.findById(
                        gameRelatedIdSource.getId(
                            it,
                            this.containerId
                        )
                    ).get()
                }
            return container?.let {
                ContainerGameResponse(it)
            } ?: ErrorGameResponse("container not found")
        }
    }


}