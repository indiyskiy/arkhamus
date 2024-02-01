package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyTickRequestMessage

interface NettyRequestHandler {

    fun acceptClass(nettyRequestMessage: NettyTickRequestMessage): Boolean
    fun accept(nettyRequestMessage: NettyTickRequestMessage): Boolean
    fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ): GameResponseMessage
}