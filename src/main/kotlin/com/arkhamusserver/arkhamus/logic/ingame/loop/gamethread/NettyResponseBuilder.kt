package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class NettyResponseBuilder(
    private val responseMapper: List<NettyResponseMapper>,
) {
    fun buildResponse(
        response: GameResponseMessage,
        requestContainer: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame
    ): NettyResponseMessage {
        return responseMapper.first {
            it.acceptClass(response) && it.accept(response)
        }.process(
            response,
            requestContainer.nettyRequestMessage,
            requestContainer.userAccount,
            requestContainer.gameSession,
            requestContainer.userRole,
        )
    }

}
