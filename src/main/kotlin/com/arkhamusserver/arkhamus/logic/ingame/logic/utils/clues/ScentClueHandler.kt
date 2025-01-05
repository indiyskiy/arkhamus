package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisScentClueRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.ScentClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ScentClueHandler(
    private val scentClueRepository: ScentClueRepository,
    private val redisScentClueRepository: RedisScentClueRepository,
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler
) : AdvancedClueHandler {

    companion object {
        const val MAX_ON_GAME = 7
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun isTargetScentBad(target: WithTrueIngameId, data: GlobalGameData): Boolean {
        val godScent = data.game.god.getTypes().contains(Clue.SCENT)
        if (!godScent) {
            return false
        }
        if (target is RedisScentClue) {
            return target.turnedOn
        }
        return false
    }

    override fun accept(clues: List<Clue>): Boolean {
        return clues.contains(Clue.SCENT)
    }

    override fun accept(clue: Clue): Boolean {
        return clue == Clue.SCENT
    }

    override fun accept(target: WithStringId): Boolean {
        return target is RedisScentClue
    }

    override fun canBeAdded(container: CluesContainer): Boolean {
        return container.scent.any { !it.turnedOn }
    }

    override fun addClue(
        data: GlobalGameData
    ) {
        data.clues.scent.filter { !it.turnedOn }.random(random).apply {
            turnedOn = true
            redisScentClueRepository.save(this)
        }
    }

    override fun canBeRemoved(container: CluesContainer): Boolean {
        return container.scent.any { it.turnedOn }
    }

    override fun canBeRemoved(
        user: RedisGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        val scent = target as RedisScentClue
        return userLocationHandler.userCanSeeTargetInRange(
            whoLooks = user,
            target = scent,
            levelGeometryData = data.levelGeometryData,
            range = scent.interactionRadius,
            affectedByBlind = true,
        )
    }

    override fun anyCanBeRemoved(
        user: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        return data.clues.scent.any {
            canBeRemoved(user, it, data)
        }
    }

    override fun removeRandom(container: CluesContainer) {
        val scentClue = container.scent.filter { it.turnedOn }.randomOrNull()
        scentClue?.let {
            it.turnedOn = false
            redisScentClueRepository.save(it)
        }
    }

    override fun removeTarget(
        target: WithStringId,
        container: CluesContainer
    ) {
        val scentClue = container.scent.find { it.inGameId() == target.stringId().toLong() } ?: return
        scentClue.turnedOn = false
        redisScentClueRepository.save(scentClue)
    }

    override fun addClues(
        session: GameSession,
        god: God,
        zones: List<RedisLevelZone>,
        activeCluesOnStart: Int
    ) {
        val scentClues = scentClueRepository.findByLevelId(session.gameSessionSettings.level!!.id!!)
        val scentCluesForGameSession = scentClues.shuffled(random).take(MAX_ON_GAME)
        val redisScentClues = scentCluesForGameSession.map {
            RedisScentClue(
                id = generateRandomId(),
                gameId = session.id!!,
                redisScentId = it.inGameId,
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
            val turnedOn = redisScentClues.shuffled(random).take(activeCluesOnStart)
            turnedOn.forEach {
                it.turnedOn = true
            }
        }
        redisScentClueRepository.saveAll(redisScentClues)
    }

    override fun mapActualClues(
        container: CluesContainer,
        user: RedisGameUser,
        levelGeometryData: LevelGeometryData,
    ): List<ExtendedClueResponse> {
        return container.scent.filter {
            it.turnedOn == true
        }.filter {
            userLocationHandler.userCanSeeTarget(user, it, levelGeometryData, true)
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
                possibleRadius = 0.0,
                turnedOn = true,
            )
        }
    }

    override fun mapPossibleClues(
        container: CluesContainer,
        user: RedisGameUser,
        levelGeometryData: LevelGeometryData,
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
                possibleRadius = 0.0,
                turnedOn = false
            )
        }
    }

}