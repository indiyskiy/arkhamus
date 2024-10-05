package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorUserState
import com.arkhamusserver.arkhamus.model.redis.RedisDoor
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.DoorResponse
import org.springframework.stereotype.Component

@Component
class DoorDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        myUser: RedisGameUser,
        doors: Collection<RedisDoor>,
        levelGeometryData: LevelGeometryData
    ): List<DoorResponse> {
        return doors.map { door ->
            val state = door.globalState
            val response = DoorResponse(
                doorId = door.inGameId(),
                doorState = when (state) {
                    DoorState.OPEN -> if (inBanList(myUser, door)) {
                        DoorUserState.CLOSED_PERSONALLY
                    } else {
                        DoorUserState.OPEN
                    }

                    DoorState.CLOSED -> DoorUserState.CLOSED
                }
            )
            mask(response, door, myUser, levelGeometryData)
            response
        }
    }

    private fun inBanList(
        user: RedisGameUser,
        door: RedisDoor
    ): Boolean {
        return door.closedForUsers.contains(user.userId)
    }

    private fun mask(
        responseToMask: DoorResponse,
        door: RedisDoor,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (
            !userLocationHandler.userCanSeeTarget(myUser, door, levelGeometryData, false) ||
            !visibilityByTagsHandler.userCanSeeTarget(myUser, door)
        ) {
            responseToMask.doorState = DoorUserState.CLOSED
        }
    }

}