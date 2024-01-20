package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.view.dto.netty.request.GetContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenChessNettyRequestHandler(
    private val containerRepository: ContainerRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyRequestMessage): Boolean =
        nettyRequestMessage::class.java == GetContainerRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyRequestMessage): Boolean = true


    override fun process(
        nettyRequestMessage: NettyRequestMessage,
        user: UserAccount?,
        gameSession: GameSession?,
        arkhamusChannel: ArkhamusChannel
    ): GameResponseMessage {
        return with(nettyRequestMessage as GetContainerRequestMessage) {
            val container =
                containerRepository.findById(
                    gameRelatedIdSource.getId(gameSession!!.id!!,nettyRequestMessage.containerId)
                ).get()
            ContainerGameResponse(container)
        }
    }


}