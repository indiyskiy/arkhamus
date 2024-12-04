package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ContainerState
import org.springframework.stereotype.Component

@Component
class ContainerDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        myUser: RedisGameUser,
        containers: List<RedisContainer>,
        levelGeometryData: LevelGeometryData
    ): List<ContainerState> {
        return containers.map { container ->
            val response = ContainerState(container)
            mask(response, container, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: ContainerState,
        container: RedisContainer,
        currentUser: RedisGameUser,
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
            responseToMask.state = MapObjectState.DISABLED
            responseToMask.holdingUserId = null
            responseToMask.gameTags = setOf()
        } else {
            responseToMask.gameTags = responseToMask.gameTags.filter {
                visibilityByTagsHandler.userCanSeeTarget(currentUser, InGameObjectTag.valueOf(it))
            }.toMutableSet()
        }
    }

}