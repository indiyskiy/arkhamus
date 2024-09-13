package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import com.arkhamusserver.arkhamus.model.redis.WithPoint
import org.springframework.stereotype.Component

const val USER_LUMINOUS_RADIUS = 5.0 //m

@Component
class UserLocationHandler(
    private val geometryUtils: GeometryUtils,
) {

    fun userCanSeeTarget(
        whoLooks: RedisGameUser,
        target: WithPoint,
        levelGeometryData: LevelGeometryData,
        geometryAffectsVision: Boolean = true,
    ): Boolean {
        return haveGlobalVision(whoLooks) || (
                inVisionDistance(whoLooks, target) &&
                        onHighGroundOrSameLevel(whoLooks, target) &&
                        geometryCheck()
                )
    }

    private fun onHighGroundOrSameLevel(
        whoLooks: RedisGameUser,
        target: WithPoint
    ): Boolean {
        return geometryUtils.onHighGroundOrSameLevel(whoLooks, target)
    }

    private fun haveGlobalVision(whoLooks: RedisGameUser): Boolean =
        whoLooks.stateTags.contains(UserStateTag.FARSIGHT.name)

    private fun geometryCheck(
        //TODO implement true geometry handler
    ): Boolean = true

    fun inVisionDistance(
        whoLooks: RedisGameUser,
        target: WithPoint,
    ): Boolean {
        return distanceLessOrEquals(whoLooks, target, GlobalGameSettings.GLOBAL_VISION_DISTANCE)
    }

    fun distanceLessOrEquals(
        point1: WithPoint,
        point2: WithPoint,
        maxDistance: Double
    ): Boolean {
        return geometryUtils.distanceLessOrEquals(point1, point2, maxDistance)
    }

    fun isInDarkness(user: RedisGameUser, globalGameData: GlobalGameData): Boolean {
        val nearLantern = nearLantern(user, globalGameData.lanterns.values)
        val nearLuminousUser = nearLuminousUser(user, globalGameData.users.values)
        return !nearLantern && !nearLuminousUser
    }

    private fun nearLuminousUser(user: RedisGameUser, users: Collection<RedisGameUser>): Boolean {
        return user.stateTags.contains(UserStateTag.LUMINOUS.name) ||
                users.any { otherUser ->
                    otherUser.stateTags.contains(UserStateTag.LUMINOUS.name) &&
                            geometryUtils.distanceLessOrEquals(
                                user,
                                otherUser,
                                USER_LUMINOUS_RADIUS
                            )
                }
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