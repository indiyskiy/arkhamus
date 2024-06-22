package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.getOtherGameUsers
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
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
    ): NettyResponseAuth? {
        if (
            user?.id != null && gameSession != null && userRole != null
        ) {
            val gameUser = redisDataAccess.getGameUser(user.id, gameSession.id) ?: return null
            val otherUsers = redisDataAccess.getOtherGameUsers(user.id, gameSession.id)

            return NettyResponseAuth(
                message = AuthState.SUCCESS,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(gameUser),
                allGameUsers = otherUsers.map {
                    GameUserResponse(it)
                }
            )
        } else {
            return NettyResponseAuth(
                AuthState.FAIL,
                -1L,
                0L,
                MyGameUserResponse(
                    id = 0,
                    nickName = "",
                    madness = 0.0,
                    madnessNotches = listOf(100.0, 300.0, 600.0),
                    x = 0.0,
                    y = 0.0,
                ),
                emptyList()
            )
        }
    }

}