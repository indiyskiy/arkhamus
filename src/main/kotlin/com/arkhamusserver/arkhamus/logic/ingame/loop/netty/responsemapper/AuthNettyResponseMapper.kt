package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseAuth
import org.springframework.stereotype.Component

@Component
class AuthNettyResponseMapper  {

     fun process(
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): NettyResponseAuth = if (
        user != null && gameSession != null && userRole != null
    ) {
        NettyResponseAuth(AuthState.SUCCESS, 0L)
    } else {
        NettyResponseAuth(AuthState.FAIL, 0L)
    }

}