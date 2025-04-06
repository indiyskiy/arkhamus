package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedClueResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ClueHandler(
    private val advancedClueHandlers: List<AdvancedClueHandler>,
    private val activityHandler: ActivityHandler,
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
        private val logger = LoggerFactory.getLogger(ClueHandler::class.java)
    }

    fun filterClues(
        clues: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): ExtendedCluesResponse {
        val possibleClues = mapPossibleClues(clues, user, data)
        val actualClues = mapActualClues(clues, user, data)
        return ExtendedCluesResponse(possibleClues, actualClues)
    }

    private fun mapActualClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return advancedClueHandlers.flatMap {
            it.mapActualClues(container, user, data)
        }
    }

    private fun mapPossibleClues(
        container: CluesContainer,
        user: InGameUser,
        data: GlobalGameData,
    ): List<ExtendedClueResponse> {
        return advancedClueHandlers.flatMap {
            it.mapPossibleClues(container, user, data)
        }
    }

    fun addRandomClue(
        data: GlobalGameData,
        sourceUser: InGameUser?,
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
                gameId = data.game.gameId,
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
            it.canBeRemovedRandomly(existingClues)
        }?.removeRandom(existingClues)
    }

    fun removeClueByAbility(
        data: GlobalGameData,
        target: WithStringId
    ) {
        advancedClueHandlers.firstOrNull {
            it.acceptForRemoveAbility(target)
        }?.removeTarget(target, data)
    }

    fun canBeRemovedByAbility(
        user: InGameUser,
        target: Any,
        data: GlobalGameData
    ): Boolean {
        logger.info("canBeRemovedByAbility: $target")
        val handler = advancedClueHandlers.firstOrNull {
            it.acceptForRemoveAbility(target as WithStringId)
        }
        if (handler == null) {
            logger.info("canBeRemovedByAbility: handler is null")
            return false
        }
        return target is WithStringId &&
                handler.acceptForRemoveAbility(target) &&
                handler.canBeRemovedByAbility(user, target, data)
    }

    fun anyCanBeRemovedByAbility(user: InGameUser, data: GlobalGameData): Boolean {
        return advancedClueHandlers.any {
            it.anyCanBeRemovedByAbility(user, data)
        }
    }

}