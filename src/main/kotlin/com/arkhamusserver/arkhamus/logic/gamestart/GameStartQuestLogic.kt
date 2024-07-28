package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.UserQuestCreationHandler
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameStartQuestLogic(
    private val redisQuestRepository: RedisQuestRepository,
    private val questRepository: QuestRepository,
    private val userQuestCreationHandler: UserQuestCreationHandler
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartQuestLogic::class.java)
    }

    fun createQuests(
        levelId: Long,
        game: GameSession,
        users: List<RedisGameUser>,
    ) {
        val allLevelQuests = questRepository.findByLevelIdAndQuestState(levelId, QuestState.ACTIVE)
        val createdRedisQuests = allLevelQuests.map { dbQuest ->
            with(createQuest(game, dbQuest)) {
                redisQuestRepository.save(this)
            }
        }
        users.forEach { user ->
            userQuestCreationHandler.setStartsQuestsForUser(user, createdRedisQuests)
        }
    }

    private fun createQuest(
        game: GameSession,
        dbQuest: Quest,
    ) = RedisQuest(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        questId = dbQuest.id!!,
        gameId = game.id!!,
        startQuestGiverId = dbQuest.startQuestGiver.inGameId,
        endQuestGiverId = dbQuest.endQuestGiver.inGameId,
        levelTaskIds = dbQuest.questSteps.sortedBy { it.stepNumber }.map { it.levelTask.inGameId }.toMutableList(),
        difficulty = dbQuest.dificulty
    )

}