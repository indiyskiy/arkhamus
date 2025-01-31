package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.LanternData
import org.springframework.stereotype.Component

@Component
class LanternDataHandler(
    private val userLocationHandler: UserLocationHandler,
) {
    fun map(
        myUser: InGameGameUser,
        lanterns: Collection<InGameLantern>,
        levelGeometryData: LevelGeometryData
    ): List<LanternData> {
        return lanterns.map { lantern ->
            val response = LanternData(
                lanternId = lantern.inGameId(),
                lanternState = lantern.lanternState,
                objectState = lantern.state,
                lightRange = lantern.lightRange,
            )
            mask(response, lantern, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: LanternData,
        lantern: InGameLantern,
        myUser: InGameGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeTarget(
                whoLooks = myUser,
                target = lantern,
                levelGeometryData = levelGeometryData,
                affectedByBlind = false,
                heightAffectVision = false,
                geometryAffectsVision = false
            )
        ) {
            responseToMask.lanternState = LanternState.EMPTY
            responseToMask.objectState = MapObjectState.DISABLED
        }
    }

}