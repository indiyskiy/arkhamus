package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage

interface NettyRequestHandler {

    fun acceptClass(nettyRequestMessage: NettyRequestMessage): Boolean
    fun accept(nettyRequestMessage: NettyRequestMessage): Boolean
    fun process(
        nettyRequestMessage: NettyRequestMessage,
        user: UserAccount?,
        gameSession: GameSession?,
        arkhamusChannel: ArkhamusChannel
    ): GameResponseMessage
}