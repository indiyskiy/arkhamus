package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseAuth
import org.springframework.stereotype.Component

@Component
class AuthNettyResponseMapper(
    private val redisDataAccess: RedisDataAccess
) {
    fun process(
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): NettyResponseAuth = if (
        user?.id != null && gameSession != null && userRole != null
    ) {
        val gameUser = redisDataAccess.getGameUser(user.id, gameSession.id)
        val otherUsers = redisDataAccess.getOtherGameUsers(user.id, gameSession.id)

        NettyResponseAuth(
            AuthState.SUCCESS,
            0L,
            user.id!!,
            MyGameUserResponseMessage(
                user.id!!,
                gameUser.nickName,
                gameUser.x,
                gameUser.y
            ),
            otherUsers.map {
                NettyGameUserResponseMessage(
                    it.userId, it.nickName, it.x, it.y
                )
            }
        )
    } else {
        NettyResponseAuth(AuthState.FAIL, 0L, 0L, MyGameUserResponseMessage(0, "", 0.0, 0.0), emptyList())
    }

}