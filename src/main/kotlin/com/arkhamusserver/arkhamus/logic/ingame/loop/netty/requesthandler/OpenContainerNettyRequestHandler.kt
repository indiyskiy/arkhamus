package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.view.dto.netty.request.GetContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyRequestHandler(
    private val containerRepository: ContainerRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val gameUserRedisRepository: GameUserRedisRepository,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == GetContainerRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ): GameResponseMessage {
        val request = nettyTickRequestMessageContainer.nettyRequestMessage
        with(request as GetContainerRequestMessage) {
            nettyTickRequestMessageContainer.gameSession?.id?.let {
                val container =
                    containerRepository.findById(
                        gameRelatedIdSource.getId(
                            it,
                            this.containerId
                        )
                    ).get()

                val user = gameUserRedisRepository.findById(
                    gameRelatedIdSource.getId(
                        it,
                        nettyTickRequestMessageContainer.userAccount.id!!
                    )
                ).get()
                return ContainerGameResponse(container, user)
            } ?: return ErrorGameResponse("game session id is null")
        }
    }


}