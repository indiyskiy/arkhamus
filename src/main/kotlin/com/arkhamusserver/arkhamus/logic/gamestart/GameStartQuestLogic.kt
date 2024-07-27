package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min
import kotlin.random.Random

@Component
class GameStartQuestLogic(
    private val redisQuestRepository: RedisQuestRepository,
    private val questRepository: QuestRepository,
    private val redisUserQuestProgressRepository: RedisUserQuestProgressRepository
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartQuestLogic::class.java)
        val random: Random = Random(System.currentTimeMillis())
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
            setQuestsForUser(user, createdRedisQuests)
        }
    }

    private fun setQuestsForUser(user: RedisGameUser, createdRedisQuests: List<RedisQuest>) {
        val quests =
            createdRedisQuests.shuffled(random).take(min(QUESTS_ON_START, createdRedisQuests.size)).toMutableList()
        val userStartQuests = quests.map { quest ->
            RedisUserQuestProgress(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = quest.gameId,
                questId = quest.questId,
                userId = user.userId,
            )
        }
        redisUserQuestProgressRepository.saveAll(userStartQuests)
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
        levelTaskId = dbQuest.questSteps.sortedBy { it.stepNumber }.map { it.levelTask.inGameId }.toMutableList(),
        difficulty = dbQuest.dificulty
    )

}