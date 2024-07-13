package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Component

@Component
class UserLocationHandler(
    private val geometryUtils: GeometryUtils,
    private val zonesHandler: ZonesHandler
) {

    fun userCanSeeUser(
        user1: RedisGameUser,
        user2: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        return inVisionDistance(user1, user2) && zonesHandler.inSameZoneOrNotInZone(user1, user2, levelGeometryData)
    }

    fun inVisionDistance(
        user1: RedisGameUser,
        user2: RedisGameUser,
    ): Boolean {
        return distanceLessOrEquals(user1, user2, GlobalGameSettings.GLOBAL_VISION_DISTANCE)
    }

    fun distanceLessOrEquals(
        user1: RedisGameUser,
        user2: RedisGameUser,
        maxDistance: Double
    ): Boolean {
        return geometryUtils.distanceLessOrEquals(user1, user2, maxDistance)
    }

    fun isInDarkness(user: RedisGameUser, globalGameData: GlobalGameData): Boolean {
        return !nearLantern(user, globalGameData.lanterns.values)
    }

    private fun nearLantern(user: RedisGameUser, lanterns: Collection<RedisLantern>): Boolean {
        return lanterns.any { lantern ->
            geometryUtils.distanceLessOrEquals(
                user,
                lantern,
                lantern.lightRange
            )
        }
    }
}