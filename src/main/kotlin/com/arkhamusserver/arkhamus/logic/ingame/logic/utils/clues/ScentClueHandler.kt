package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameScentClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.ScentClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameScentClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ScentClueHandler(
    private val scentClueRepository: ScentClueRepository,
    private val inGameScentClueRepository: InGameScentClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        private val random: Random = Random(System.currentTimeMillis())
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.SCENT)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.SCENT
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameScentClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.scent.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.scent.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameScentClueRepository.save(this)
        }
    }

    override fun canBeRemovedRandomly(container: CluesContainer): Boolean {
        return container.scent.any { it.turnedOn }
    }

    override fun canBeRemovedByAbility(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val scent = target as InGameScentClue
        return scent.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = scent,
            levelGeometryData = data.levelGeometryData,
            range = scent.interactionRadius,
            affectedByBlind = true,
        )
    }

    override fun anyCanBeRemovedByAbility(
        user: InGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.scent.any {
            canBeRemovedByAbility(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val scentClue = container.scent.filter { it.turnedOn }.randomOrNull()
        scentClue?.let {
            it.turnedOn = false
            inGameScentClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val scentClue = data.clues.scent.find { it.inGameId() == target.stringId().toLong() } ?: return
        scentClue.turnedOn = false
        inGameScentClueRepository.save(scentClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val scentClues = scentClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val scentCluesForGameSession = scentClues.shuffled(random).take(MAX_ON_GAME)
        val inGameScentClues = scentCluesForGameSession.map {
            InGameScentClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameScentId = it.inGameId,
                x = it.x,
                y = it.y,
                z = it.z,
                interactionRadius = it.interactionRadius,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_SCENT,
                ),
                turnedOn = false
            )
        }
        if (god.getTypes().contains(Clue.SCENT)) {
            val turnedOn = inGameScentClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameScentClueRepository.saveAll(inGameScentClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.scent.filter {
            it.turnedOn == true
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, data.levelGeometryData, true)
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.SCENT)
        }.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.SCENT,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.SCENT_CLUE,
                x = null,
                y = null,
                z = null,
                state = ClueState.ACTIVE_CLUE,
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val scentOptions = container.scent
        val filteredByVisibilityTags = scentOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.SCENT,
                relatedObjectId = it.inGameId(),
                relatedObjectType = GameObjectType.SCENT_CLUE,
                x = null,
                y = null,
                z = null,
                state = countState(it, user),
            )
        }
    }

    private fun countState(
        clue: InGameScentClue,
        user: InGameUser,
    ): ClueState {
        return if (clue.castedAbilityUsers.contains(user.inGameId())) {
            if (clue.turnedOn) {
                ClueState.ACTIVE_CLUE
            } else {
                ClueState.ACTIVE_NO_CLUE
            }
        } else {
            ClueState.ACTIVE_UNKNOWN
        }
    }

}