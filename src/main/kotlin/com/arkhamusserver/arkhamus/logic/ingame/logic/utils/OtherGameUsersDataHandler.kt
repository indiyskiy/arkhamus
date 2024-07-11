package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import org.springframework.stereotype.Component

@Component
class OtherGameUsersDataHandler(
    private val userLocationHandler: UserLocationHandler
) {
    fun map(
        myUser: RedisGameUser,
        otherGameUsers: List<RedisGameUser>,
        levelGeometryData: LevelGeometryData
    ): List<GameUserResponse> {
        val allUsers = otherGameUsers.map { thatUser ->
            val response = GameUserResponse(thatUser)
            mask(response, thatUser, myUser, levelGeometryData)
            response
        }
        return allUsers
    }

    private fun mask(
        responseToMask: GameUserResponse,
        thatUser: RedisGameUser,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeUser(myUser, thatUser, levelGeometryData)){
            responseToMask.x = null
            responseToMask.y = null
        }
    }
}