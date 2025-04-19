package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.UserQuestCreationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestStep
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
import com.arkhamusserver.arkhamus.model.ingame.InGameTask
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartQuestLogic(
    private val inGameQuestRepository: InGameQuestRepository,
    private val questRepository: QuestRepository,
    private val userQuestCreationHandler: UserQuestCreationHandler
) {

    companion object {
        private val logger = LoggingUtils.getLogger<GameStartQuestLogic>()
    }

    @Transactional
    fun createQuests(
        levelId: Long,
        game: GameSession,
        users: List<InGameUser>,
    ) {
        val allLevelQuests = questRepository.findByLevelIdAndQuestState(levelId, QuestState.ACTIVE)
        val createdInGameQuests = allLevelQuests.map { dbQuest ->
            with(createQuest(game, dbQuest)) {
                inGameQuestRepository.save(this)
            }
        }
        users.forEach {
            userQuestCreationHandler.setStartsQuestsForUser(it, createdInGameQuests)
        }
    }

    private fun createQuest(
        game: GameSession,
        dbQuest: Quest,
    ) = InGameQuest(
        id = generateRandomId(),
        questId = dbQuest.id!!,
        gameId = game.id!!,
        startQuestGiverId = dbQuest.startQuestGiver.inGameId,
        endQuestGiverId = dbQuest.endQuestGiver.inGameId,
        levelTasks = dbQuest.questSteps.sortedBy { it.stepNumber }.map {
            createTask(game.id, it)
        },
        difficulty = dbQuest.dificulty,
        textKey = dbQuest.textKey.value ?: ""
    )

    private fun createTask(
        id: Long?,
        step: QuestStep
    ): InGameTask = InGameTask(
        id = generateRandomId(),
        gameId = id!!,
        taskId = step.levelTask.inGameId,
        x = step.levelTask.x,
        y = step.levelTask.y,
        z = step.levelTask.z,
        interactionRadius = step.levelTask.interactionRadius,
        gameTags = emptySet(),
        visibilityModifiers = setOf(VisibilityModifier.ALL)
    )

}