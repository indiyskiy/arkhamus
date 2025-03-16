package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.VisibilityMap
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class UserLocationHandler(
    private val geometryUtils: GeometryUtils,
) {

    companion object {
        const val USER_LUMINOUS_RADIUS = 5.0 //m
    }

    fun userCanSeeTargetInRange(
        whoLooks: InGameUser,
        target: WithPoint,
        levelGeometryData: LevelGeometryData,
        range: Double,
        affectedByBlind: Boolean,
        heightAffectVision: Boolean = true,
        geometryAffectsVision: Boolean = true,
    ): Boolean {
        return userCanSeeTarget(
            whoLooks,
            target,
            levelGeometryData,
            affectedByBlind,
            heightAffectVision,
            geometryAffectsVision
        ) && distanceLessOrEquals(whoLooks, target, range)
    }

    fun userCanSeeTarget(
        whoLooks: InGameUser,
        target: WithPoint,
        levelGeometryData: LevelGeometryData,
        affectedByBlind: Boolean,
        heightAffectVision: Boolean = true,
        geometryAffectsVision: Boolean = true,
    ): Boolean {
        return haveGlobalVision(whoLooks) || (
                inVisionDistance(whoLooks, target, affectedByBlind) &&
                        (!heightAffectVision || !onHighGround(whoLooks, target)) &&
                        (!geometryAffectsVision || geometryCheck(whoLooks, target, levelGeometryData))
                )
    }

    fun userInInteractionRadius(user: InGameUser, interactable: Interactable): Boolean {
        return distanceLessOrEquals(user, interactable, interactable.interactionRadius())
    }

    private fun onHighGround(
        whoLooks: InGameUser,
        target: WithPoint
    ): Boolean {
        return geometryUtils.onHighGround(whoLooks, target)
    }

    private fun haveGlobalVision(whoLooks: InGameUser): Boolean =
        whoLooks.stateTags.contains(UserStateTag.FARSIGHT)

    private fun geometryCheck(
        whoLooks: InGameUser,
        target: WithPoint,
        levelGeometryData: LevelGeometryData
    ): Boolean {
        return checkVisibility(whoLooks, target, levelGeometryData.visibilityMap!!)
    }

    fun inVisionDistance(
        whoLooks: InGameUser,
        target: WithPoint,
        affectedByBlind: Boolean,
    ): Boolean {
        val distance =
            if (affectedByBlind && whoLooks.additionalData.madness.madnessDebuffs.contains(
                    MadnessDebuffs.BLIND.name
                )
            ) {
                GlobalGameSettings.GLOBAL_VISION_DISTANCE * 0.75
            } else {
                GlobalGameSettings.GLOBAL_VISION_DISTANCE
            }
        return distanceLessOrEquals(whoLooks, target, distance)
    }

    fun distanceLessOrEquals(
        point1: WithPoint,
        point2: WithPoint,
        maxDistance: Double
    ): Boolean {
        return geometryUtils.distanceLessOrEquals(point1, point2, maxDistance)
    }

    fun isInDarkness(user: InGameUser, globalGameData: GlobalGameData): Boolean {
        val nearLantern = nearLantern(user, globalGameData.lanterns)
        val nearLuminousUser = nearLuminousUser(user, globalGameData.users.values)
        return !(nearLantern || nearLuminousUser)
    }

    private fun nearLuminousUser(user: InGameUser, users: Collection<InGameUser>): Boolean {
        return user.stateTags.contains(UserStateTag.LUMINOUS) ||
                users.any { otherUser ->
                    otherUser.stateTags.contains(UserStateTag.LUMINOUS) &&
                            geometryUtils.distanceLessOrEquals(
                                user,
                                otherUser,
                                USER_LUMINOUS_RADIUS
                            )
                }
    }

    private fun nearLantern(user: InGameUser, lanterns: Collection<InGameLantern>): Boolean {
        return lanterns.any { lantern ->
            geometryUtils.distanceLessOrEquals(
                user,
                lantern,
                lantern.lightRange
            ) && lantern.lanternState == LanternState.LIT &&
                    lantern.fuel > 0.0
        }
    }

    private fun checkVisibility(from: WithPoint, to: WithPoint, visibilityMap: VisibilityMap): Boolean {
        val visibilityMapSegment = visibilityMap.findVisibilitySegment(from, to)
        // TODO get rid of this weird null exception handling
        if (visibilityMapSegment == null) {
            throw RuntimeException("Visibility checker failure, should never happen!!")
        }
        val res = visibilityMapSegment.obstacles.fold(true) { acc, obstacle ->
            acc && !obstacle.blocksVision(from, to)
        }
        return res
    }
}