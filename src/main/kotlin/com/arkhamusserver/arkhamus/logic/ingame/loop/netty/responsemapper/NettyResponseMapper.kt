package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage

interface NettyResponseMapper {

    fun acceptClass(gameResponseMessage: GameResponseMessage): Boolean
    fun accept(gameResponseMessage: GameResponseMessage): Boolean
    fun process(
        gameResponseMessage: GameResponseMessage,
        nettyRequestMessage: NettyRequestMessage,
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): NettyResponseMessage
}