package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.UserQuestCreationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
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
        val logger: Logger = LoggerFactory.getLogger(GameStartQuestLogic::class.java)
    }

    @Transactional
    fun createQuests(
        levelId: Long,
        game: GameSession,
        users: List<InGameGameUser>,
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
        levelTaskIds = dbQuest.questSteps.sortedBy { it.stepNumber }.map { it.levelTask.inGameId }.toMutableList(),
        difficulty = dbQuest.dificulty,
        textKey = dbQuest.textKey.value ?: ""
    )

}