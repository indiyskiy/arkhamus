package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import org.springframework.stereotype.Component

@Component
class GameResponseBuilder(
    private val requestHandlers: List<NettyRequestHandler>,
) {
    fun buildResponse(
        container: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData
    ): GameResponseMessage {
        val nettyRequestMessage = container.nettyRequestMessage
        return requestHandlers.first {
            it.acceptClass(nettyRequestMessage) && it.accept(nettyRequestMessage)
        }.process(container, globalGameData)
    }


}
