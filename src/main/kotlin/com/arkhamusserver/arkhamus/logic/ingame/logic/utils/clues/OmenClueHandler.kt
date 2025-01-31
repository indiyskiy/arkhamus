package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameOmenClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InnovateClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameOmenClue
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class OmenClueHandler(
    private val inGameOmenClueRepository: InGameOmenClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        private val random: Random = Random(System.currentTimeMillis())
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.OMEN)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.OMEN
    }

    override fun accept(target: WithStringId): Boolean {
        return target is InGameGameUser
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.omen.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.omen.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            inGameOmenClueRepository.save(this)
        }
    }

    override fun canBeRemovedRabdomly(container: CluesContainer): Boolean {
        return container.omen.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: InGameGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val targetUser = (target as? InGameGameUser) ?: return false
        val omen = data.clues.omen.find { it.userId == targetUser.inGameId() } ?: return false
        return omen.turnedOn && userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = targetUser,
            levelGeometryData = data.levelGeometryData,
            range = omen.interactionRadius,
            affectedByBlind = true,
        )
    }

    override fun anyCanBeRemoved(
        user: InGameGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.users.any {
            canBeRemoved(user, it.value, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val omenClue = container.omen.filter { it.turnedOn }.randomOrNull()
        omenClue?.let {
            it.turnedOn = false
            inGameOmenClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val gameUser = (gameObjectFinder.findById(target.stringId(), GameObjectType.CHARACTER, data) as? InGameGameUser) ?: return
        val omenClue = data.clues.omen.find { it.userId == gameUser.inGameId() } ?: return
        omenClue.turnedOn = false
        inGameOmenClueRepository.save(omenClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<InGameLevelZone>,
        activeCluesOnStart: Int
    ) {
        val users = session.usersOfGameSession
        val omenCluesForGameSession = users.shuffled(random).take(MAX_ON_GAME)
        val inGameOmenClues = omenCluesForGameSession.map {
            InGameOmenClue(
                id = generateRandomId(),
                gameId = session.id!!,
                inGameOmenId = it.id!!,
                interactionRadius = Ability.ADVANCED_SEARCH_FOR_OMEN.range!!,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_OMEN,
                ),
                turnedOn = false,
                userId = it.id!!,
            )
        }
        if (god.getTypes().contains(Clue.OMEN)) {
            val turnedOn = inGameOmenClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        inGameOmenClueRepository.saveAll(inGameOmenClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: InGameGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return container.omen.filter {
            it.turnedOn == true
        }.filter {
            val targetUser = data.users[it.userId] ?: return emptyList()
            userLocationHandler.userCanSeeTarget(user, targetUser, data.levelGeometryData, true)
        }.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, Clue.OMEN)
        }.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.OMEN,
                relatedObjectId = it.userId,
                relatedObjectType = GameObjectType.CHARACTER,
                x = null,
                y = null,
                z = null,
                possibleRadius = 0.0,
                state = InnovateClueState.ACTIVE_CLUE,
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: InGameGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        val omenOptions = container.omen
        val filteredByVisibilityTags = omenOptions.filter {
            visibilityByTagsHandler.userCanSeeTarget(user, it)
        }
        return filteredByVisibilityTags.map {
            ExtendedClueResponse(
                id = it.id,
                clue = Clue.OMEN,
                relatedObjectId = it.userId,
                relatedObjectType = GameObjectType.CHARACTER,
                x = null,
                y = null,
                z = null,
                possibleRadius = 0.0,
                state = countState(it, user),
            )
        }
    }

    private fun countState(
        clue: InGameOmenClue,
        user: InGameGameUser,
    ): InnovateClueState {
        return if (clue.castedAbilityUsers.contains(user.inGameId())) {
            if (clue.turnedOn) {
                InnovateClueState.ACTIVE_CLUE
            } else {
                InnovateClueState.ACTIVE_NO_CLUE
            }
        } else {
            InnovateClueState.ACTIVE_UNKNOWN
        }
    }

}