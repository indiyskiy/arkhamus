package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ClueHandler(
    private val advancedClueHandlers: List<AdvancedClueHandler>,
    private val activityHandler: ActivityHandler,
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun filterClues(
        clues: CluesContainer,
        user: RedisGameUser,
        data: GlobalGameData,
    ): ExtendedCluesResponse {
        val possibleClues = mapPossibleClues(clues, user, data)
        val actualClues = mapActualClues(clues, user, data)
        return ExtendedCluesResponse(possibleClues, actualClues)
    }

    private fun mapActualClues(
        container: CluesContainer,
        user: RedisGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return advancedClueHandlers.flatMap {
            it.mapActualClues(container, user, data)
        }
    }

    private fun mapPossibleClues(
        container: CluesContainer,
        user: RedisGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return advancedClueHandlers.flatMap {
            it.mapPossibleClues(container, user, data)
        }
    }

    fun addRandomClue(
        data: GlobalGameData,
        sourceUser: RedisGameUser?,
        createActivity: Boolean = false,
    ) {
        val existingClues = data.clues
        val clueTypes = data.game.god.getTypes()
        val clueTypesCanBeAdded = clueTypes.filter { clueType ->
            advancedClueHandlers.firstOrNull {
                it.accept(clueType)
            }?.canBeAdded(existingClues) == true
        }
        val clueTypeToAdd = clueTypesCanBeAdded.randomOrNull(random) ?: return
        val added = advancedClueHandlers.firstOrNull {
            it.accept(clueTypeToAdd)
        }?.addClue(data)

        if (added != null && sourceUser != null && createActivity) {
            activityHandler.addUserNotTargetActivity(
                gameId = data.game.gameId!!,
                activityType = ActivityType.CLUE_CREATED,
                sourceUser = sourceUser,
                gameTime = data.game.globalTimer,
                relatedEventId = clueTypeToAdd.ordinal.toLong()
            )
        }
    }

    fun removeRandomClue(data: GlobalGameData) {
        val existingClues = data.clues
        advancedClueHandlers.shuffled(random).firstOrNull {
            it.canBeRemovedRabdomly(existingClues)
        }?.removeRandom(existingClues)
    }

    fun removeClue(
        data: GlobalGameData,
        target: WithStringId
    ) {
        advancedClueHandlers.firstOrNull {
            it.accept(target)
        }?.removeTarget(target, data)
    }

    fun canBeRemoved(
        user: RedisGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        return advancedClueHandlers.any {
            it is WithStringId &&
                    it.accept(target as WithStringId) &&
                    it.canBeRemoved(user, target, data)
        }
    }

    fun anyCanBeRemoved(user: RedisGameUser, data: GlobalGameData): Boolean {
        return advancedClueHandlers.any {
            it.anyCanBeRemoved(user, data)
        }
    }

}