package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameAuraClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.AuraClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.EllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Ellipse
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Tetragon
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.AuraClue
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameAuraClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.parts.AuraCluePoint
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.AdditionalClueDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.AuraClueAdditionalDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SimpleCoordinates
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Component
class AuraClueHandler(
    private val auraClueRepository: AuraClueRepository,
    private val inGameAuraClueRepository: InGameAuraClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
    private val ellipseRepository: EllipseRepository,
    private val tetragonRepository: TetragonRepository,
    private val geometryUtils: GeometryUtils
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        const val DEFAULT_INTERACTION_RADIUS = 1.0
        private val random: Random = Random(System.currentTimeMillis())
        private val logger = LoggerFactory.getLogger(AuraClueHandler::class.java)
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.AURA)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.AURA
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameAuraClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.aura.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.aura.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameAuraClueRepository.save(this)
        }
    }

    override fun canBeRemovedRandomly(container: CluesContainer): Boolean {
        return container.aura.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val aura = target as InGameAuraClue
        return aura.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = aura,
            levelGeometryData = data.levelGeometryData,
            range = aura.interactionRadius,
            affectedByBlind = true,
        )
    }

    override fun anyCanBeRemoved(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.aura.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val auraClue = container.aura.filter { it.turnedOn }.randomOrNull()
        auraClue?.let {
            it.turnedOn = false
            inGameAuraClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val auraClue = data.clues.aura.find { it.inGameId() == target.stringId().toLong() } ?: return
        auraClue.turnedOn = false
        inGameAuraClueRepository.save(auraClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val ellipses = ellipseRepository.findByLevelZoneLevelId(session.gameSessionSettings.level!!.id!!)
        val tetragons = tetragonRepository.findByLevelZoneLevelId(session.gameSessionSettings.level!!.id!!)
        logger.info("searching in ${ellipses.size} ellipses and ${tetragons.size} tetragons")

        val auraClues = auraClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val auraCluesForGameSession = auraClues.shuffled(random).take(MAX_ON_GAME)
        val inGameAuraClues = auraCluesForGameSession.map {
            InGameAuraClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameAuraId = it.inGameId,
                x = it.x,
                y = it.y,
                z = it.z,
                interactionRadius = it.interactionRadius,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_AURA,
                ),
                turnedOn = false,
                targetPoint = generatePoint(it, tetragons, ellipses),
                castedAbilityUsers = emptySet()
            )
        }
        if (god.getTypes().contains(Clue.AURA)) {
            val turnedOn = inGameAuraClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameAuraClueRepository.saveAll(inGameAuraClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.aura.filter {
            it.turnedOn == true
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.AURA)
        }.map {
            val percentage = countPercentage(user, it.targetPoint)
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.AURA,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.AURA_CLUE,
                x = null,
                y = null,
                z = null,
                state = ClueState.ACTIVE_CLUE,
                additionalData = countActualAdditionalData(it, percentage),
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val auraOptions = container.aura
        val filteredByVisibilityTags = auraOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            val percentage = countPercentage(user, it.targetPoint)
            val state = countState(it, user, percentage)
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.AURA,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.AURA_CLUE,
                x = null,
                y = null,
                z = null,
                state = state,
                additionalData = countAdditionalData(it, user, percentage, state),
            )
        }
    }

    private fun countAdditionalData(
        clue: InGameAuraClue,
        user: InGameUser,
        percentage: Int,
        state: ClueState
    ): AuraClueAdditionalDataResponse? {
        val visible = clue.castedAbilityUsers.contains(user.inGameId())
        val seeActualState = state in setOf(ClueState.ACTIVE_CLUE, ClueState.ACTIVE_NO_CLUE)
        return if (visible) {
            AuraClueAdditionalDataResponse(
                distancePercentage = percentage,
                pointReached = percentage == 100,
                outOfRadius = percentage == -100,
                targetPoint = if (seeActualState) {
                    SimpleCoordinates(
                        x = clue.targetPoint.x,
                        y = clue.targetPoint.y,
                        z = clue.targetPoint.z
                    )
                } else null
            )
        } else null
    }

    private fun countActualAdditionalData(
        clue: InGameAuraClue,
        percentage: Int,
    ): AdditionalClueDataResponse? {
        return if (percentage == -100) {
            AuraClueAdditionalDataResponse(
                distancePercentage = -100,
                pointReached = false,
                outOfRadius = true,
                targetPoint = SimpleCoordinates(
                    x = clue.targetPoint.x,
                    y = clue.targetPoint.y,
                    z = clue.targetPoint.z
                )
            )
        } else {
            AuraClueAdditionalDataResponse(
                distancePercentage = 100,
                pointReached = true,
                outOfRadius = false,
                targetPoint = SimpleCoordinates(
                    x = clue.targetPoint.x,
                    y = clue.targetPoint.y,
                    z = clue.targetPoint.z
                )
            )
        }
    }

    fun countPercentage(user: InGameUser, auraCluePoint: AuraCluePoint): Int {
        // Calculate the current distance between the user and the circle's center
        val currentDistance = geometryUtils.distance(user, auraCluePoint)

        val denominator = 2 * auraCluePoint.startDistance - auraCluePoint.interactionRadius
        if (denominator == 0.0) {
            logger.warn("Potential division by zero detected: 2 * startDistance equals circle.radius!")
            return if (currentDistance >= auraCluePoint.interactionRadius) -100 else 100 // Safeguard result
        }
        return when {
            // User is inside or on the circle's boundary
            currentDistance <= auraCluePoint.interactionRadius -> {
                100
            }
            // User is at start distance
            currentDistance == auraCluePoint.startDistance -> {
                0
            }
            // User is at twice the starting distance or farther
            currentDistance >= 2 * auraCluePoint.startDistance -> {
                -100
            }
            // For distances between startDistance and 2 * startDistance
            else -> {
                // Linearly interpolate the percentage value
                percentage(currentDistance, auraCluePoint, denominator).toInt()
            }
        }
    }

    private fun percentage(
        currentDistance: Double,
        auraCluePoint: AuraCluePoint,
        denominator: Double
    ): Double =
        100 - ((currentDistance - auraCluePoint.interactionRadius) / denominator * 200)


    private fun countState(
        clue: InGameAuraClue,
        user: InGameUser,
        percentage: Int
    ): ClueState {
        return if (clue.castedAbilityUsers.contains(user.inGameId()) && percentage == 100) {
            if (clue.turnedOn) {
                ClueState.ACTIVE_CLUE
            } else {
                ClueState.ACTIVE_NO_CLUE
            }
        } else {
            ClueState.ACTIVE_UNKNOWN
        }
    }

    private fun generateRandomPoint(clue: AuraClue): AuraCluePoint {
        val minRadius = clue.minSpawnRadius
        val maxRadius = clue.maxSpawnRadius
        val randomRadius = sqrt(Random.nextDouble(minRadius * minRadius, maxRadius * maxRadius))
        logger.info("Radius  {} < {} < {}", minRadius, randomRadius, maxRadius)

        // Generate a random angle between 0 and 2π (360 degrees)
        val randomAngle = random.nextDouble(0.0, 2 * Math.PI)
        logger.info("randomAngle: {}", randomAngle)

        // Convert polar coordinates to Cartesian coordinates
        val x = clue.x + randomRadius * cos(randomAngle)
        val z = clue.z + randomRadius * sin(randomAngle)
        logger.info("random point generated x: {}, z: {}", x, z)
        return AuraCluePoint(
            x = x,
            y = 0.0,
            z = z,
            interactionRadius = DEFAULT_INTERACTION_RADIUS,
            id = generateRandomId(),
            visibilityModifiers = setOf(VisibilityModifier.HAVE_ITEM_AURA),
            startDistance = randomRadius - DEFAULT_INTERACTION_RADIUS,
        )
    }

    private fun isPointInZone(
        tetragons: List<GeometryUtils.Tetragon>,
        ellipses: List<GeometryUtils.Ellipse>,
        point: AuraCluePoint
    ): Boolean {
        return tetragons.any { it ->
            geometryUtils.contains(it, point)
        } || ellipses.any {
            geometryUtils.contains(it, point)
        }
    }

    private fun generatePoint(
        clue: AuraClue,
        tetragons: List<Tetragon>,
        ellipses: List<Ellipse>
    ): AuraCluePoint {
        if (clue.zone?.id == null) {
            logger.error("Invalid clue: zone or zone ID is null. Clue: {}", clue)
            throw IllegalArgumentException("Clue or associated zone is invalid. Unable to generate point.")
        }
        val zoneId = clue.zone!!.inGameId
        val filteredTetragons = tetragons.filter {
            it.levelZone.inGameId == zoneId
        }.map {
            GeometryUtils.Tetragon(
                p0 = GeometryUtils.Point(it.point0X, it.point0Z),
                p1 = GeometryUtils.Point(it.point1X, it.point1Z),
                p2 = GeometryUtils.Point(it.point2X, it.point2Z),
                p3 = GeometryUtils.Point(it.point3X, it.point3Z),
            )
        }
        val filteredEllipses = ellipses.filter { ellipse ->
            ellipse.levelZone.inGameId == zoneId
        }.map {
            GeometryUtils.Ellipse(
                center = GeometryUtils.Point(it.x, it.y),
                rz = it.height / 2,
                rx = it.width / 2
            )
        }
        logger.info("creating AURA CLUE point in ${filteredEllipses.size} ellipses and ${filteredTetragons.size} tetragons")
        // Safety measure: Attempt to generate valid points
        val maxAttempts = 1000
        repeat(maxAttempts) {
            val point = generateRandomPoint(clue)
            if (isPointInZone(filteredTetragons, filteredEllipses, point)) {
                logger.info("Generated a valid AuraCluePoint: {}", point)
                return point
            }
        }
        logger.error(
            "Failed to generate a valid AuraCluePoint after {} attempts for Clue: {} in Zone: {}",
            maxAttempts, clue, zoneId
        )
        logger.warn("generating random point no meter what")
        return generateRandomPoint(clue)
//        throw IllegalStateException("Unable to generate a valid AuraCluePoint after $maxAttempts attempts.")
    }
}