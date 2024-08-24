package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import org.springframework.stereotype.Component

@Component
class GameDataBuilder(
    private val requestHandlers: List<NettyRequestHandler>,
) {
    fun build(
        container: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>,
    ): RequestProcessData {
        val nettyRequestMessage = container.nettyRequestMessage
        return requestHandlers.first {
            it.acceptClass(nettyRequestMessage) && it.accept(nettyRequestMessage)
        }.buildData(container, globalGameData, ongoingEvents)
    }

}
