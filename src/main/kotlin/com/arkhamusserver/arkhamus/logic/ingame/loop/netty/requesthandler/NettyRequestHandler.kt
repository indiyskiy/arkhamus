package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

interface NettyRequestHandler {

    fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean
    fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean
    fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ): GameResponseMessage
}