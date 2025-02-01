package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import org.springframework.stereotype.Component

@Component
class OtherGameUsersDataHandler(
    private val userLocationHandler: UserLocationHandler
) {
    fun map(
        myUser: InGameUser,
        otherGameUsers: List<InGameUser>,
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
        thatUser: InGameUser,
        myUser: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeTarget(
                whoLooks = myUser,
                target = thatUser,
                levelGeometryData = levelGeometryData,
                affectedByBlind = true
            )
        ) {
            responseToMask.x = null
            responseToMask.y = null
            responseToMask.z = null
            responseToMask.stateTags = emptySet()
        }
        responseToMask.stateTags = responseToMask.stateTags.filter {
            it.getVisibility() == Visibility.PUBLIC
        }.toSet()
    }
}