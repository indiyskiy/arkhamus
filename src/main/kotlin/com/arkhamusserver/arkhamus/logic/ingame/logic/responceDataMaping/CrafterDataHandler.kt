package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CrafterState
import org.springframework.stereotype.Component

@Component
class CrafterDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        currentUser: InGameUser,
        crafters: List<InGameCrafter>,
        levelGeometryData: LevelGeometryData
    ): List<CrafterState> {
        return crafters.map { crafter ->
            val response = CrafterState(crafter)
            mask(response, crafter, currentUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: CrafterState,
        crafter: InGameCrafter,
        currentUser: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (
            !userLocationHandler.userCanSeeTarget(
                whoLooks = currentUser,
                target = crafter,
                levelGeometryData = levelGeometryData,
                affectedByBlind = true
            ) ||
            !visibilityByTagsHandler.userCanSeeTarget(currentUser, crafter)
        ) {
            responseToMask.state = MapObjectState.NOT_IN_SIGHT
            responseToMask.holdingUserId = null
            responseToMask.gameTags = setOf()
        } else {
            responseToMask.gameTags = responseToMask.gameTags.filter {
                visibilityByTagsHandler.userCanSeeTarget(currentUser, InGameObjectTag.valueOf(it))
            }.toMutableSet()
        }
    }

}