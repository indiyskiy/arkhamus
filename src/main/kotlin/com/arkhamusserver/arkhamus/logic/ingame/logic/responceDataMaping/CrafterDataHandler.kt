package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CrafterState
import org.springframework.stereotype.Component

@Component
class CrafterDataHandler(
    private val userLocationHandler: UserLocationHandler
) {
    fun map(
        myUser: RedisGameUser,
        crafters: List<RedisCrafter>,
        levelGeometryData: LevelGeometryData
    ): List<CrafterState> {
        return crafters.map { crafter ->
            val response = CrafterState(crafter)
            mask(response, crafter, myUser, levelGeometryData)
            response
        }
    }

    private fun mask(
        responseToMask: CrafterState,
        crafter: RedisCrafter,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeTarget(myUser, crafter, levelGeometryData)) {
            responseToMask.state = MapObjectState.ACTIVE
            responseToMask.holdingUserId = null
        }
    }

}