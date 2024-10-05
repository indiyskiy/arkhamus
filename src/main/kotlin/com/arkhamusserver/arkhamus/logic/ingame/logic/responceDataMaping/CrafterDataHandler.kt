package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CrafterState
import org.springframework.stereotype.Component

@Component
class CrafterDataHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) {
    fun map(
        currentUser: RedisGameUser,
        crafters: List<RedisCrafter>,
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
        crafter: RedisCrafter,
        currentUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (
            !userLocationHandler.userCanSeeTarget(currentUser, crafter, levelGeometryData) ||
            !visibilityByTagsHandler.userCanSeeTarget(currentUser, crafter)
        ) {
            responseToMask.state = MapObjectState.ACTIVE
            responseToMask.holdingUserId = null
        } else {
            responseToMask.gameTags = responseToMask.gameTags.filter {
                visibilityByTagsHandler.userCanSeeTarget(currentUser, it)
            }
        }
    }

}