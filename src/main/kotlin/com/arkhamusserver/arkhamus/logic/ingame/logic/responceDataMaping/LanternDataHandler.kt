package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.LanternData
import org.springframework.stereotype.Component

@Component
class LanternDataHandler(
    private val userLocationHandler: UserLocationHandler,
) {
    fun map(
        myUser: InGameUser,
        lanterns: Collection<InGameLantern>,
        levelGeometryData: LevelGeometryData
    ): List<LanternData> {
        return lanterns.map { lantern ->
            val response = LanternData(
                lanternId = lantern.inGameId(),
                lanternState = lantern.lanternState,
                state = lantern.state,
                lightRange = lantern.lightRange,
            )
            mask(response, lantern, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: LanternData,
        lantern: InGameLantern,
        myUser: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        val canSeeLantern = userLocationHandler.userCanSeeTarget(
            whoLooks = myUser,
            target = lantern,
            levelGeometryData = levelGeometryData,
            affectedByBlind = true,
        )
        val inVisionDistance = userLocationHandler.inVisionDistance(
            whoLooks = myUser,
            target = lantern,
            false
        )
        maskState(canSeeLantern, responseToMask)
        mapLanternState(inVisionDistance, responseToMask, lantern, canSeeLantern)
    }

    private fun mapLanternState(
        inVisionDistance: Boolean,
        responseToMask: LanternData,
        lantern: InGameLantern,
        canSeeLantern: Boolean
    ) {
        if (!inVisionDistance) {
            responseToMask.lanternState = LanternState.EMPTY
        } else {
            if (lantern.lanternState != LanternState.LIT && !canSeeLantern) {
                responseToMask.lanternState = LanternState.EMPTY
            }
        }
    }

    private fun maskState(
        canSeeLantern: Boolean,
        responseToMask: LanternData
    ) {
        if (!canSeeLantern) {
            responseToMask.state = MapObjectState.NOT_IN_SIGHT
        }
    }

}