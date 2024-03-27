package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer

interface NettyRequestProcessor {
    fun accept(request: NettyTickRequestMessageContainer): Boolean
    fun process(
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    )
}