package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component

@Component
class GameResponseBuilder(
    private val requestHandlers: List<NettyRequestHandler>,
) {
    fun buildResponse(
        container: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame
    ): GameResponseMessage {
        val nettyRequestMessage = container.nettyRequestMessage
        return requestHandlers.first {
            it.acceptClass(nettyRequestMessage) && it.accept(nettyRequestMessage)
        }.process(container)
    }


}
