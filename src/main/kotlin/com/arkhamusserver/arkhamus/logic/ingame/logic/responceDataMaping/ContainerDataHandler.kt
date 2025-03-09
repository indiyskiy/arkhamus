package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ContainerStateResponse
import org.springframework.stereotype.Component

@Component
class ContainerDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        myUser: InGameUser,
        containers: List<InGameContainer>,
        levelGeometryData: LevelGeometryData
    ): List<ContainerStateResponse> {
        return containers.map { container ->
            val response = ContainerStateResponse(container)
            mask(response, container, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: ContainerStateResponse,
        container: InGameContainer,
        currentUser: InGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (
            !userLocationHandler.userCanSeeTarget(
                whoLooks = currentUser,
                target = container,
                levelGeometryData = levelGeometryData,
                affectedByBlind = true
            ) ||
            !visibilityByTagsHandler.userCanSeeTarget(currentUser, container)
        ) {
            responseToMask.state = MapObjectState.NOT_ACHIEVABLE
            responseToMask.holdingUserId = null
            responseToMask.gameTags = setOf()
        } else {
            responseToMask.gameTags = responseToMask.gameTags.filter {
                visibilityByTagsHandler.userCanSeeTarget(currentUser, InGameObjectTag.valueOf(it))
            }.toMutableSet()
        }
    }

}