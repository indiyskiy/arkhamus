package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import org.springframework.stereotype.Component

@Component
class BaseRequestProcessor : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return true
    }

    override fun process(
        request: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val nettyRequestMessage = request.nettyRequestMessage
        val oldGameUser = globalGameData.users[request.userAccount.id]!!
        oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
        oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
    }
}