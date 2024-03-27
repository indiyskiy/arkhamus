package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.view.dto.netty.request.AbilityRequestMessage
import org.springframework.stereotype.Component

@Component
class AbilityRequestProcessor(
    val requestProcessDataBuilder: GameDataBuilder
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return request.nettyRequestMessage is AbilityRequestMessage
    }

    override fun process(
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        requestContainer.requestProcessData = requestProcessDataBuilder.build(requestContainer, globalGameData, ongoingEvents)
    }
}