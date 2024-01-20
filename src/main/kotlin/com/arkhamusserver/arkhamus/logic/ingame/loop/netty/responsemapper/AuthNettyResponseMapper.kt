package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseAuth
import org.springframework.stereotype.Component

@Component
class AuthNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: GameResponseMessage): Boolean =
        gameResponseMessage::class.java == AuthGameResponse::class.java

    override fun accept(gameResponseMessage: GameResponseMessage): Boolean = true
    override fun process(
        gameResponseMessage: GameResponseMessage,
        nettyRequestMessage: NettyRequestMessage,
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): NettyResponseAuth = if (
        user != null && gameSession != null && userRole != null
    ) {
        NettyResponseAuth(AuthState.SUCCESS)
    } else {
        NettyResponseAuth(AuthState.FAIL)
    }

}