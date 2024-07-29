package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.ContainerState
import org.springframework.stereotype.Component

@Component
class ContainerDataHandler(
    private val geometryUtils: GeometryUtils,
    private val zonesHandler: ZonesHandler
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
        thatUser: RedisContainer,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ) {
        if (!near(myUser, thatUser) ||
            !notHidden(thatUser, myUser, levelGeometryData)
        ) {
            responseToMask.state = MapObjectState.ACTIVE
            responseToMask.holdingUserId = null
        }
    }

    private fun notHidden(
        container: RedisContainer,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        return zonesHandler.inSameZoneOrNotInZone(container, myUser, levelGeometryData)
    }

    private fun near(
        myUser: RedisGameUser,
        thatUser: RedisContainer
    ) = geometryUtils.distanceLessOrEquals(myUser, thatUser, GlobalGameSettings.GLOBAL_VISION_DISTANCE)
}