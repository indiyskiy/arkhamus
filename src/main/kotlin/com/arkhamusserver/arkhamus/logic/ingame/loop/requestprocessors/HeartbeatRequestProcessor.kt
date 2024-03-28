package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import org.springframework.stereotype.Component

@Component
class HeartbeatRequestProcessor : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return request.nettyRequestMessage is HeartbeatRequestMessage
    }

    override fun process(
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {

    }
}