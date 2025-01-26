package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisOmenClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InnovateClueState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.clues.RedisOmenClue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class OmenClueHandler(
    private val redisOmenClueRepository: RedisOmenClueRepository,
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
        return target is RedisGameUser
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.omen.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.omen.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            redisOmenClueRepository.save(this)
        }
    }

    override fun canBeRemovedRabdomly(container: CluesContainer): Boolean {
        return container.omen.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: RedisGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val targetUser = (target as? RedisGameUser) ?: return false
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
        user: RedisGameUser,
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
            redisOmenClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        data: GlobalGameData
    ) {
        val gameUser = (gameObjectFinder.findById(target.stringId(), GameObjectType.CHARACTER, data) as? RedisGameUser) ?: return
        val omenClue = data.clues.omen.find { it.userId == gameUser.inGameId() } ?: return
        omenClue.turnedOn = false
        redisOmenClueRepository.save(omenClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<RedisLevelZone>,
        activeCluesOnStart: Int
    ) {
        val users = session.usersOfGameSession
        val omenCluesForGameSession = users.shuffled(random).take(MAX_ON_GAME)
        val redisOmenClues = omenCluesForGameSession.map {
            RedisOmenClue(
                id = generateRandomId(),
                gameId = session.id!!,
                redisOmenId = it.id!!,
                interactionRadius = Ability.ADVANCED_SEARCH_FOR_OMEN.range!!,
                visibilityModifiers = setOf(
                    VisibilityModifier.HAVE_ITEM_OMEN,
                ),
                turnedOn = false,
                userId = it.id!!,
            )
        }
        if (god.getTypes().contains(Clue.OMEN)) {
            val turnedOn = redisOmenClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        redisOmenClueRepository.saveAll(redisOmenClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: RedisGameUser,
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
        user: RedisGameUser,
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
        clue: RedisOmenClue,
        user: RedisGameUser,
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