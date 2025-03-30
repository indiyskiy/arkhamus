package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserStatusResponse
import org.springframework.stereotype.Component

@Component
class UserInGameStatusMapper {
    fun mapStatuses(
        user: InGameUser,
        data: GlobalGameData
    ): List<UserStatusResponse> {
        return data.userStatuses.filter {
            it.userId == user.inGameId()
        }.map {
            UserStatusResponse(
                status = it.status,
                started = it.started,
            )
        }
    }
}