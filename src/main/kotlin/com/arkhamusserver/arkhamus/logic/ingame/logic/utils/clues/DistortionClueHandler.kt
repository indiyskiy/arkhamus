package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameDistortionClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.DistortionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.DistortionClue
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InnovateClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameDistortionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.DistortionClueAdditionalDataResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.SimpleUserAdditionalDataResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DistortionClueHandler(
    private val distortionClueRepository: DistortionClueRepository,
    private val inGameDistortionClueRepository: InGameDistortionClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        private val random: Random = Random(System.currentTimeMillis())
        private val logger = LoggerFactory.getLogger(DistortionClueHandler::class.java)
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.DISTORTION)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.DISTORTION
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameDistortionClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.distortion.any { !it.turnedOn && it.receiver != null }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.distortion.filter {
            !it.turnedOn && it.receiver != null
        }.random(random).apply {
            turnedOn = true
            receiver?.turnedOn = true
            inGameDistortionClueRepository.save(this)
        }
    }

    override fun canBeRemovedRabdomly(container: CluesContainer): Boolean {
        return container.distortion.any { it.turnedOn && it.receiver != null }
    }

    override fun canBeRemoved(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val distortion = target as InGameDistortionClue
        return distortion.turnedOn &&
                distortion.receiver != null &&
                userLocationHandler.userCanSeeTargetInRange(
                    whoLooks = user,
                    target = distortion,
                    levelGeometryData = data.levelGeometryData,
                    range = distortion.interactionRadius,
                    affectedByBlind = true,
                )
    }

    override fun anyCanBeRemoved(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.distortion.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val distortionClue = container.distortion.filter {
            it.turnedOn && it.receiver != null
        }.randomOrNull()
        distortionClue?.let {
            it.turnedOn = false
            distortionClue.receiver?.turnedOn = false
            inGameDistortionClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val distortionClue = data.clues.distortion.find { it.inGameId() == target.stringId().toLong() } ?: return
        distortionClue.turnedOn = false
        distortionClue.receiver?.turnedOn = false
        inGameDistortionClueRepository.save(distortionClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        logger.info("Starting to add DISTORTION clues for session: ${session.id}, god: ${god.name}, activeCluesOnStart: $activeCluesOnStart")

        val distortionClues = distortionClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        logger.info("Found ${distortionClues.size} DISTORTION clues for level: ${session.gameSessionSettings.level!!.id}")

        val distortionClueTransmitters = distortionClues
            .filter { it.canTransmit }
            .shuffled(random)
            .take(MAX_ON_GAME)
        logger.info("Selected ${distortionClueTransmitters.size} DISTORTION transmitters for game session.")

        val transmitterIds = distortionClueTransmitters.map { it.inGameId }.toSet()
        val distortionClueReceiversForGameSession = distortionClues
            .filter { it.canReceive }
            .filter { it.inGameId !in transmitterIds }
            .shuffled(random)
            .take(distortionClueTransmitters.size)
        logger.info("Selected ${distortionClueReceiversForGameSession.size} DISTORTION receivers for game session.")

        val transmitterReceiverPairs =
            distortionClueTransmitters.zip(distortionClueReceiversForGameSession)
        logger.info("Created ${transmitterReceiverPairs.size} transmitter-receiver pairs.")

        val inGameDistortionClues = transmitterReceiverPairs.map { (transmitter, receiver) ->
            mapNewClue(session, transmitter, receiver)
        }
        logger.info("Mapped ${inGameDistortionClues.size} new in-game distortion clues.")

        if (god.getTypes().contains(Clue.DISTORTION)) {
            val turnedOn = inGameDistortionClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
                it.receiver?.turnedOn = true
                logger.info("Turned on clue with id: ${it.id} and its receiver.")
            }
        } else {
            logger.info("god have no distortion clues")
        }

        inGameDistortionClueRepository.saveAll(inGameDistortionClues)
        logger.info("Saved ${inGameDistortionClues.size} distortion clues for session: ${session.id}")
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.distortion.filter {
            it.turnedOn == true
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.DISTORTION)
        }.map {
            ExtendedClueResponse(
                id = it.stringId(),
                clue = Clue.DISTORTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.DISTORTION_CLUE,
                x = null,
                y = null,
                z = null,
                state = InnovateClueState.ACTIVE_CLUE,
                additionalData = mapTransmitterAdditionalData(it, user, data)
            )
        }
    }


    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return mapTransmitters(container, user, data) + mapReceivers(container, user, data)
    }

    private fun mapTransmitters(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val distortionOptions = container.distortion
        val filteredByVisibilityTags = distortionOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.stringId(),
                clue = Clue.DISTORTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.DISTORTION_CLUE,
                x = null,
                y = null,
                z = null,
                state = InnovateClueState.ACTIVE_UNKNOWN,
                additionalData = mapTransmitterAdditionalData(it, user, data)
            )
        }
    }

    private fun mapReceivers(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val distortionOptions = container.distortion
        val filtered = distortionOptions.filter { distortionClue ->
            distortionClue.receiver != null &&
                    userLocationHandler.userCanSeeTargetInRange(
                        whoLooks = user,
                        target = distortionClue.receiver,
                        levelGeometryData = data.levelGeometryData,
                        range = distortionClue.effectRadius,
                        affectedByBlind = true
                    ) &&
                    transmitterOnline(distortionClue, data)
        }
        return filtered.map {
            ExtendedClueResponse(
                id = it.receiver!!.stringId(),
                clue = Clue.DISTORTION,
                relatedObjectId = it.receiver.inGameId(),
                relatedObjectType = GameObjectType.DISTORTION_CLUE,
                x = null,
                y = null,
                z = null,
                state = if (it.turnedOn) InnovateClueState.ACTIVE_CLUE else InnovateClueState.ACTIVE_NO_CLUE,
                additionalData = mapReceiverAdditionalData(it, data)
            )
        }
    }

    private fun transmitterOnline(
        clue: InGameDistortionClue,
        data: GlobalGameData,
    ): Boolean = clue.castedAbilityUsers.any { nearOtherSideUserId ->
        val nearOtherSideUser = data.users[nearOtherSideUserId]!!
        userLocationHandler.userCanSeeTargetInRange(
            whoLooks = nearOtherSideUser,
            target = clue,
            levelGeometryData = data.levelGeometryData,
            range = clue.effectRadius,
            affectedByBlind = true
        )
    }

    private fun mapTransmitterAdditionalData(
        clue: InGameDistortionClue,
        user: InGameUser,
        data: GlobalGameData,
    ): DistortionClueAdditionalDataResponse? {
        return if (clue.castedAbilityUsers.contains(user.inGameId())) {
            val connected = userLocationHandler.distanceLessOrEquals(
                user,
                clue,
                clue.effectRadius,
            )
            val canSee = userLocationHandler.userCanSeeTarget(
                whoLooks = user,
                target = clue,
                levelGeometryData = data.levelGeometryData,
                true
            )
            val receiver = clue.receiver!!
            DistortionClueAdditionalDataResponse(
                otherSideId = if (canSee) receiver.inGameId() else null,
                connected = connected,
                usersInSight = mapUsersInSight(receiver, connected, data)
            )
        } else {
            null
        }
    }

    private fun mapReceiverAdditionalData(
        clue: InGameDistortionClue,
        data: GlobalGameData,
    ): DistortionClueAdditionalDataResponse? {
        return DistortionClueAdditionalDataResponse(
            otherSideId = clue.inGameId(),
            connected = false,
            usersInSight = mapUsersInSight(clue, true, data)
        )
    }

    private fun mapUsersInSight(
        otherSide: InGameDistortionClue,
        active: Boolean,
        data: GlobalGameData
    ): List<SimpleUserAdditionalDataResponse> {
        if (!active) return emptyList()
        return data.users.values.filter {
            userLocationHandler.userCanSeeTargetInRange(
                it,
                otherSide,
                data.levelGeometryData,
                otherSide.effectRadius,
                false
            )
        }.map {
            SimpleUserAdditionalDataResponse().apply {
                id = it.inGameId()
                nickName = it.nickName
                skin = it.originalSkin
                x = it.x
                y = it.y
                z = it.z
            }
        }
    }

    private fun mapNewClue(
        session: GameSession,
        transmitter: DistortionClue,
        receiver: DistortionClue?,
    ): InGameDistortionClue = InGameDistortionClue(
        id = generateRandomId(),
        gameId = session.id!!,
        inGameDistortionId = transmitter.inGameId,
        x = transmitter.x,
        y = transmitter.y,
        z = transmitter.z,
        interactionRadius = transmitter.interactionRadius,
        visibilityModifiers = setOf(
            VisibilityModifier.HAVE_ITEM_DISTORTION,
        ),
        turnedOn = false,
        effectRadius = transmitter.effectRadius,
        receiver = receiver?.let {
            mapNewClue(session, it, null)
        }
    )

}