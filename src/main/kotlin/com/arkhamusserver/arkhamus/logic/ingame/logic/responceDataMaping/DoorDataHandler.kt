package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorUserState
import com.arkhamusserver.arkhamus.model.ingame.InGameDoor
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.DoorResponse
import org.springframework.stereotype.Component

@Component
class DoorDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        myUser: InGameUser,
        doors: Collection<InGameDoor>,
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
        user: InGameUser,
        door: InGameDoor
    ): Boolean {
        return door.closedForUsers.contains(user.inGameId())
    }

    private fun mask(
        responseToMask: DoorResponse,
        door: InGameDoor,
        myUser: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (
            !userLocationHandler.userCanSeeTarget(
                whoLooks = myUser,
                target = door,
                levelGeometryData = levelGeometryData,
                affectedByBlind = false,
                heightAffectVision = false,
                geometryAffectsVision = false
            ) ||
            !visibilityByTagsHandler.userCanSeeTarget(myUser, door)
        ) {
            responseToMask.doorState = DoorUserState.CLOSED
        }
    }

}