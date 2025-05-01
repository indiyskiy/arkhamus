package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.CluesContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils.EVENT_IN_GAME_SYSTEM
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
        private val logger = LoggingUtils.getLogger<ClueHandler>()
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
    ) {
        LoggingUtils.withContext(
            gameId = data.game.inGameId(),
            eventType = EVENT_IN_GAME_SYSTEM,
            userId = sourceUser?.inGameId().toString()
        ) {
            logger.info("addRandomClue start")
        }
        val existingClues = data.clues
        val clueTypes = data.game.god.getTypes()
        val clueTypesCanBeAdded: List<Clue> = clueTypes.filter { clueType ->
            advancedClueHandlers.firstOrNull {
                it.accept(clueType)
            }?.canBeAdded(existingClues) == true
        }
        val clueTypeToAdd = clueTypesCanBeAdded.randomOrNull(random)
        clueTypeToAdd?.let { clueTypeToAddNotNull ->
            val added = advancedClueHandlers.firstOrNull {
                it.accept(clueTypeToAddNotNull)
            }?.addClue(data)

            if (added != null && sourceUser != null) {
                activityHandler.addUserNotTargetActivity(
                    gameId = data.game.gameId,
                    activityType = ActivityType.CLUE_CREATED,
                    sourceUser = sourceUser,
                    gameTime = data.game.globalTimer,
                    relatedEventId = clueTypeToAddNotNull.ordinal.toLong()
                )
                LoggingUtils.withContext(
                    gameId = data.game.inGameId(),
                    eventType = EVENT_IN_GAME_SYSTEM,
                    userId = sourceUser.inGameId().toString()
                ) {
                    logger.info("Clue type $clueTypeToAddNotNull added. added=$added sourceUser=$sourceUser")
                }
                if (clueTypeToAddNotNull == Clue.SCENT) {
                    removeRandomClue(data)
                }
            } else {
                LoggingUtils.withContext(
                    gameId = data.game.inGameId(),
                    eventType = EVENT_IN_GAME_SYSTEM,
                    userId = sourceUser?.inGameId().toString()
                ) {
                    logger.info("Clue type $clueTypeToAddNotNull can't be added. added=$added sourceUser=$sourceUser")
                }
            }
        } ?: {
            LoggingUtils.withContext(
                gameId = data.game.inGameId(),
                eventType = EVENT_IN_GAME_SYSTEM,
                userId = sourceUser?.inGameId().toString()
            ) {
                logger.info(
                    "No clue types can be added. clue types: ${
                        clueTypes.joinToString(",") { it.name }
                    }")
            }
        }
        LoggingUtils.withContext(
            gameId = data.game.inGameId(),
            eventType = EVENT_IN_GAME_SYSTEM,
            userId = sourceUser?.inGameId().toString()
        ) {
            logger.info("addRandomClue end")
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