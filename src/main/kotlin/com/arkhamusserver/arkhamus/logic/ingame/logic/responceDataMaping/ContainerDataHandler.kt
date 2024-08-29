package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ContainerState
import org.springframework.stereotype.Component

@Component
class ContainerDataHandler(
    private val userLocationHandler: UserLocationHandler
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
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!userLocationHandler.userCanSeeTarget(myUser, container, levelGeometryData)) {
            responseToMask.state = MapObjectState.ACTIVE
            responseToMask.holdingUserId = null
        }
    }

}