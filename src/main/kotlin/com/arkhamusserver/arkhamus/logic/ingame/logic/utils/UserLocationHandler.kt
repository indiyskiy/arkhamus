package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

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

    companion object {
        private val GLOBAL_VISION_DISTANCE: Double = 10.0
    }

    fun userCanSeeUser(
        user1: RedisGameUser,
        user2: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        return inVisionDistance(user1, user2) && inSameZoneOrNotInZone(user1, user2, levelGeometryData)
    }

    fun inVisionDistance(
        user1: RedisGameUser,
        user2: RedisGameUser,
    ): Boolean {
        return distanceLessOrEquals(user1, user2, GLOBAL_VISION_DISTANCE)
    }

    fun inSameZoneOrNotInZone(
        user1: RedisGameUser,
        user2: RedisGameUser,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        val user1Zones = zonesHandler.filterByUserPosition(user1.x, user1.y, levelGeometryData)
        val user2Zones = zonesHandler.filterByUserPosition(user2.x, user2.y, levelGeometryData)
        return (user1Zones.isEmpty() && user2Zones.isEmpty()) || user1Zones.any { user2Zones.contains(it) }
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
        return lanterns.any {
            geometryUtils.distanceLessOrEquals(
                point1X = user.x,
                point1Y = user.y,
                point2X = it.x,
                point2Y = it.y,
                it.lightRange
            )
        }
    }
}