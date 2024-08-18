package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.getOtherGameUsers
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.tech.NettyResponseAuth
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import org.springframework.stereotype.Component

@Component
class AuthNettyResponseMapper(
    private val redisDataAccess: RedisDataAccess
) {
    fun process(
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        success: Boolean,
        reason: String? = null,
    ): NettyResponseAuth? {
        if (
            user?.id != null && gameSession != null && userRole != null && success
        ) {
            val gameUser = redisDataAccess.getGameUser(user.id, gameSession.id) ?: return null
            val otherUsers = redisDataAccess.getOtherGameUsers(user.id, gameSession.id)

            return NettyResponseAuth(
                message = AuthState.SUCCESS,
                reason = reason ?: "",
                userId = user.id!!,
                myGameUser = MyGameUserResponse(gameUser, emptyList()),
                allGameUsers = otherUsers.map {
                    GameUserResponse(it)
                },
            )
        } else {
            return NettyResponseAuth(
                message = AuthState.FAIL,
                reason = reason ?: "",
                tick = -1L,
                userId = 0L,
                myGameUser = MyGameUserResponse(
                    id = 0,
                    nickName = "",
                    madness = 0.0,
                    madnessNotches = listOf(0.0, 0.0, 0.0),
                    x = 0.0,
                    y = 0.0,
                ),
                allGameUsers = emptyList(),
            )
        }
    }

}