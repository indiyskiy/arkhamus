package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CrafterState
import org.springframework.stereotype.Component

@Component
class CrafterDataHandler(
    private val geometryUtils: GeometryUtils,
    private val zonesHandler: ZonesHandler
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
        thatUser: RedisCrafter,
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
        crafter: RedisCrafter,
        myUser: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        return zonesHandler.inSameZoneOrNotInZone(crafter, myUser, levelGeometryData)
    }

    private fun near(
        myUser: RedisGameUser,
        thatUser: RedisCrafter
    ) = geometryUtils.distanceLessOrEquals(myUser, thatUser, GlobalGameSettings.GLOBAL_VISION_DISTANCE)
}