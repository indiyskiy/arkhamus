package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

interface NettyRequestHandler {

    fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean
    fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean
    fun buildData(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData

}