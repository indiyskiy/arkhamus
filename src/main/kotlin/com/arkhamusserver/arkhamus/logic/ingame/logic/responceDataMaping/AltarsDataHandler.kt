package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.EasyAltarResponse
import org.springframework.stereotype.Component

@Component
class AltarsDataHandler(
    private val userLocationHandler: UserLocationHandler,
) {
    fun mapAltars(
        altars: List<InGameAltar>,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): List<EasyAltarResponse> {
        return altars.map { altar ->
            val response = EasyAltarResponse(
                altarId = altar.inGameId(),
                state = MapObjectState.ACTIVE,
                altarState = globalGameData.altarHolder?.state ?: MapAltarState.OPEN,
            )
            mask(response, altar, user, globalGameData.levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: EasyAltarResponse,
        altar: InGameAltar,
        user: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        val canSeeAltar = userLocationHandler.userCanSeeTarget(
            whoLooks = user,
            target = altar,
            levelGeometryData = levelGeometryData,
            affectedByBlind = true,
        )
        maskState(canSeeAltar, responseToMask)
    }

    private fun maskState(
        canSeeAltar: Boolean,
        responseToMask: EasyAltarResponse
    ) {
        if (!canSeeAltar) {
            responseToMask.state = MapObjectState.NOT_IN_SIGHT
        }
    }
}