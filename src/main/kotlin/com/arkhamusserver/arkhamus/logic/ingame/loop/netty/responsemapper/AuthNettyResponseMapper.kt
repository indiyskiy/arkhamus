package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseAuth
import org.springframework.stereotype.Component

@Component
class AuthNettyResponseMapper(
    private val gameUserRedisRepository: GameUserRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) {
    fun process(
        user: UserAccount?,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): NettyResponseAuth = if (
        user?.id != null && gameSession != null && userRole != null
    ) {
        val gameUser = gameUserRedisRepository.findById(
            gameRelatedIdSource.getId(gameSession.id!!, user.id!!)
        ).get()
        NettyResponseAuth(
            AuthState.SUCCESS,
            0L,
            user.id!!,
            MyGameUserResponseMessage(
                user.id!!,
                gameUser.nickName!!,
                gameUser.x!!,
                gameUser.y!!
            ),
            emptyList()
        )
    } else {
        NettyResponseAuth(AuthState.FAIL, 0L, 0L, MyGameUserResponseMessage(0, "", 0.0, 0.0), emptyList())
    }

}