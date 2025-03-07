package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameCorruptionClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.CorruptionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameCorruptionClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.CorruptionClueAdditionalDataResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CorruptionClueHandler(
    private val corruptionClueRepository: CorruptionClueRepository,
    private val inGameCorruptionClueRepository: InGameCorruptionClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        const val DEFAULT_FULLY_GROWTH_TIME = GlobalGameSettings.MINUTE_IN_MILLIS
        const val DEFAULT_NULLIFY_TIME = GlobalGameSettings.MINUTE_IN_MILLIS * 2
        private val random: Random = Random(System.currentTimeMillis())
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.CORRUPTION)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.CORRUPTION
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameCorruptionClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.corruption.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.corruption.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameCorruptionClueRepository.save(this)
        }
    }

    override fun canBeRemovedRabdomly(container: CluesContainer): Boolean {
        return container.corruption.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val corruption = target as InGameCorruptionClue
        return corruption.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = corruption,
            levelGeometryData = data.levelGeometryData,
            range = corruption.interactionRadius,
            affectedByBlind = true,
        )
    }

    override fun anyCanBeRemoved(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.corruption.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val corruptionClue = container.corruption.filter { it.turnedOn }.randomOrNull()
        corruptionClue?.let {
            it.turnedOn = false
            inGameCorruptionClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val corruptionClue = data.clues.corruption.find { it.inGameId() == target.stringId().toLong() } ?: return
        corruptionClue.turnedOn = false
        inGameCorruptionClueRepository.save(corruptionClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val corruptionClues = corruptionClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val corruptionCluesForGameSession = corruptionClues.shuffled(random).take(MAX_ON_GAME)
        val inGameCorruptionClues = corruptionCluesForGameSession.map {
            InGameCorruptionClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameCorruptionId = it.inGameId,
                x = it.x,
                y = it.y,
                z = it.z,
                interactionRadius = it.interactionRadius,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_CORRUPTION,
                ),
                turnedOn = false,
                timeUntilFullyGrowth = DEFAULT_FULLY_GROWTH_TIME,
                totalTimeUntilNullify = DEFAULT_NULLIFY_TIME
            )
        }
        if (god.getTypes().contains(Clue.CORRUPTION)) {
            val turnedOn = inGameCorruptionClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameCorruptionClueRepository.saveAll(inGameCorruptionClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.corruption.filter {
            it.turnedOn == true
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.CORRUPTION)
        }.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.CORRUPTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.CORRUPTION_CLUE,
                x = null,
                y = null,
                z = null,
                state = ClueState.ACTIVE_CLUE,
                additionalData = mapAdditionalData(it, user)
            )
        }
    }


    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val corruptionOptions = container.corruption
        val filteredByVisibilityTags = corruptionOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.CORRUPTION,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.CORRUPTION_CLUE,
                x = null,
                y = null,
                z = null,
                state = countState(it, user),
                additionalData = mapAdditionalData(it, user)
            )
        }
    }

    private fun countState(
        clue: InGameCorruptionClue,
        user: InGameUser,
    ): ClueState {
        return if (
            clue.castedAbilityUsers.contains(user.inGameId()) &&
            clue.timeFromStart >= clue.timeUntilFullyGrowth
            ) {
            if (clue.turnedOn) {
                ClueState.ACTIVE_CLUE
            } else {
                ClueState.ACTIVE_NO_CLUE
            }
        } else {
            ClueState.ACTIVE_UNKNOWN
        }
    }

    private fun mapAdditionalData(
        clue: InGameCorruptionClue,
        user: InGameUser,
    ): CorruptionClueAdditionalDataResponse? {
        return if (clue.castedAbilityUsers.contains(user.inGameId())) {
            CorruptionClueAdditionalDataResponse(
                timeUntilFullyGrowth = clue.timeUntilFullyGrowth,
                totalTimeUntilNullify = clue.totalTimeUntilNullify,
                timeFromStart = clue.timeFromStart
            )
        } else {
            null
        }
    }

}