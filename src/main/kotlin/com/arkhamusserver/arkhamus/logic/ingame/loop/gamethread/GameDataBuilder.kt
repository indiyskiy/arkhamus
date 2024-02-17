package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import org.springframework.stereotype.Component

@Component
class GameDataBuilder(
    private val requestHandlers: List<NettyRequestHandler>,
) {
    fun build(
        container: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData
    ): GameData {
        val nettyRequestMessage = container.nettyRequestMessage
        return requestHandlers.first {
            it.acceptClass(nettyRequestMessage) && it.accept(nettyRequestMessage)
        }.buildData(container, globalGameData)
    }


}
