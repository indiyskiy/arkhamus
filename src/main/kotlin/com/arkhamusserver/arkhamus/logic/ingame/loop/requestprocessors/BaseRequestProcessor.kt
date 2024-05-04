package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import org.springframework.stereotype.Component

@Component
class BaseRequestProcessor : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return true
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val nettyRequestMessage = requestDataHolder.nettyRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
        oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
    }
}