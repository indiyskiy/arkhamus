package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import com.arkhamusserver.arkhamus.model.ingame.parts.AuraCluePoint
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.AuraClueAdditionalDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SimpleCoordinates
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuraClueResponseHandler(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
    private val geometryUtils: GeometryUtils,
) {
    companion object {
        private const val SHADOW_RANGE_RADIUS = 2.5 //m
        private val logger = LoggerFactory.getLogger(AuraClueResponseHandler::class.java)
    }

    fun mapActualClues(container: CluesContainer, user: InGameUser, data: GlobalGameData): List<ExtendedClueResponse> {
        return container.aura.filter {
            it.turnedOn == true
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.AURA)
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true) ||
                    userLocationHandler.userCanSeeTarget(user, it.targetPoint, data.levelGeometryData, true)
        }.map {
            val (showSparksBasedOnDistance, percentage) = countPercentage(user, it.targetPoint)
            val seeWell = userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
            val seeShadow = userLocationHandler.userCanSeeTarget(user, it.targetPoint, data.levelGeometryData, true)

            ExtendedClueResponse(
                id = it.id,
                clue = Clue.AURA,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.AURA_CLUE,
                x = null,
                y = null,
                z = null,
                state = ClueState.ACTIVE_CLUE,
                additionalData = countActualAdditionalData(
                    user,
                    it,
                    showSparksBasedOnDistance,
                    percentage,
                    seeWell,
                    seeShadow
                ),
            )
        }
    }

    fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
    ): List<ExtendedClueResponse> {
        val auraOptions = container.aura
        val filteredByVisibilityTags = auraOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            val (auraDistanceType, percentage) = countPercentage(user, it.targetPoint)
            val state = countState(it, user, auraDistanceType)
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.AURA,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.AURA_CLUE,
                x = null,
                y = null,
                z = null,
                state = state,
                additionalData = countAdditionalData(it, user, auraDistanceType, percentage, state),
            )
        }
    }

    private fun countActualAdditionalData(
        user: InGameUser,
        clue: InGameAuraClue,
        auraDistanceType: AuraDistanceType,
        percentage: Int,
        seeWell: Boolean,
        seeShadow: Boolean
    ): AuraClueAdditionalDataResponse {
        return AuraClueAdditionalDataResponse(
            showSparks = auraDistanceType == AuraDistanceType.IN_RANGE && clue.castedAbilityUsers.contains(user.inGameId()),
            distancePercentage = percentage,
            shadowState = if (seeShadow) ClueState.ACTIVE_CLUE else ClueState.ACTIVE_UNKNOWN,
            shadowPoint = if (seeShadow) {
                mapSimpleCoordinate(clue.targetPoint)
            } else {
                null
            },
            wellState = if (seeWell || seeShadow) ClueState.ACTIVE_CLUE else ClueState.ACTIVE_UNKNOWN,
        )
    }


    private fun countAdditionalData(
        clue: InGameAuraClue,
        user: InGameUser,
        auraDistanceType: AuraDistanceType,
        percentage: Int,
        state: ClueState
    ): AuraClueAdditionalDataResponse {
        return AuraClueAdditionalDataResponse(
            showSparks = auraDistanceType == AuraDistanceType.IN_RANGE && clue.castedAbilityUsers.contains(user.inGameId()),
            distancePercentage = percentage,
            shadowState = if (auraDistanceType == AuraDistanceType.ON_POINT) state else ClueState.ACTIVE_UNKNOWN,
            shadowPoint = if (auraDistanceType == AuraDistanceType.ON_POINT) mapSimpleCoordinate(clue.targetPoint) else null,
            wellState = if (auraDistanceType == AuraDistanceType.ON_POINT) state else ClueState.ACTIVE_UNKNOWN,
        )
    }

    private fun countState(
        clue: InGameAuraClue,
        user: InGameUser,
        auraDistanceType: AuraDistanceType
    ): ClueState {
        return if (clue.castedAbilityUsers.contains(user.inGameId()) && auraDistanceType == AuraDistanceType.ON_POINT) {
            if (clue.turnedOn) ClueState.ACTIVE_CLUE else ClueState.ACTIVE_NO_CLUE
        } else {
            ClueState.ACTIVE_UNKNOWN
        }
    }


    private fun countPercentage(user: InGameUser, auraCluePoint: AuraCluePoint): Pair<AuraDistanceType, Int> {
        // Calculate the current distance between the user and the circle's center
        val currentDistance = geometryUtils.distance(user, auraCluePoint)
        logger.info("distance {}, radius {}", currentDistance, SHADOW_RANGE_RADIUS)
        val denominator = 2 * auraCluePoint.startDistance - SHADOW_RANGE_RADIUS
        if (denominator == 0.0) {
            logger.warn("Potential division by zero detected: 2 * startDistance equals circle.radius!")
            return if (currentDistance >= SHADOW_RANGE_RADIUS) {
                AuraDistanceType.OUT_OF_RANGE to -100
            } else {
                AuraDistanceType.ON_POINT to 100
            }// Safeguard result
        }
        return when {
            // User is inside or on the circle's boundary
            currentDistance <= SHADOW_RANGE_RADIUS -> {
                logger.info("User is inside or on the circle's boundary, 100")
                AuraDistanceType.ON_POINT to 100
            }
            // User is at twice the starting distance or farther
            currentDistance >= 2 * auraCluePoint.startDistance -> {
                logger.info("User is at twice the starting distance or farther, -100")
                AuraDistanceType.OUT_OF_RANGE to -100
            }
            // For distances between startDistance and 2 * startDistance
            else -> {
                // Linearly interpolate the percentage value
                val percentage = percentage(currentDistance, denominator).toInt()
                logger.info("Linearly interpolate the percentage value, {}", percentage)
                AuraDistanceType.IN_RANGE to percentage
            }
        }
    }

    private fun percentage(
        currentDistance: Double,
        denominator: Double
    ): Double =
        100 - ((currentDistance - SHADOW_RANGE_RADIUS) / denominator * 200)

    private fun mapSimpleCoordinate(point: AuraCluePoint): SimpleCoordinates? {
        return SimpleCoordinates(
            point.x,
            point.y,
            point.z
        )
    }

    enum class AuraDistanceType {
        OUT_OF_RANGE, IN_RANGE, ON_POINT
    }
}