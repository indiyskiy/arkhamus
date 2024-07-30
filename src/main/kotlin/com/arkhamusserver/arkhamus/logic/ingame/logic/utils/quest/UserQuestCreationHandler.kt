package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_TO_REFRESH
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.math.min
import kotlin.random.Random

@Component
class UserQuestCreationHandler(
    private val redisUserQuestProgressRepository: RedisUserQuestProgressRepository,
) {
    companion object {
        val random: Random = Random(System.currentTimeMillis())
    }

    fun needToAddQuests(
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): Boolean {
        val userInProgress = userQuestsProgresses
            .filter {
                it.questState in setOf(
                    AWAITING,
                    READ,
                    IN_PROGRESS
                )
            }
        return userInProgress.size < QUESTS_TO_REFRESH
    }

    fun addQuests(
        data: QuestDeclineRequestProcessData,
        levelQuests: List<RedisQuest>,
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): List<RedisUserQuestProgress> {
        val userInProgress = userQuestsProgresses
            .filter {
                it.questState in setOf(
                    AWAITING,
                    READ,
                    IN_PROGRESS
                )
            }
        val userInProgressIds = userInProgress.map { it.questId }
        val availableByNpc = availableByNpc(levelQuests, userQuestsProgresses)
        val availableByNpcIds = availableByNpc.map { it.questId }.toSet()

        val finished = userQuestsProgresses.filter { it.questState == FINISHED }
        val declined = userQuestsProgresses.filter { it.questState == DECLINED }

        val finishedIds = finished.map { it.questId }
        val declinedIds = declined.map { it.questId }

        val notInProgress = levelQuests.filter { it.questId !in userInProgressIds && it.questId in availableByNpcIds }
        if (notInProgress.isNotEmpty()) {
            return userQuestsProgresses
        }
        val notDeclined = notInProgress.filter { it.questId !in declinedIds }
        val availableQuests = notDeclined.filter { it.questId !in finishedIds }

        val toAdd = QUESTS_ON_START - userInProgress.size
        if (toAdd > 0) {
            if (availableQuests.size >= toAdd) {
                return addQuests(data, availableQuests, toAdd)
            } else {
                cleanUserQuestLog(finished, declined)
                return addQuests(data, notInProgress, toAdd)
            }
        }
        return userQuestsProgresses
    }

    fun setStartsQuestsForUser(user: RedisGameUser, createdRedisQuests: List<RedisQuest>) {
        val questsWithUniqueQuestGivers = createdRedisQuests
            .shuffled(random)
            .distinctBy { it.startQuestGiverId }
        val quests =
            questsWithUniqueQuestGivers
                .shuffled(random)
                .take(min(QUESTS_ON_START, questsWithUniqueQuestGivers.size))
                .toMutableList()
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

    private fun availableByNpc(
        levelQuests: List<RedisQuest>,
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): List<RedisQuest> {
        val notAvailableNpcs = userQuestsProgresses.map { it.questId to it.questState }.map { pair ->
            levelQuests.first { it.questId == pair.first } to pair.second
        }.mapNotNull {
            when (it.second) {
                AWAITING -> it.first.startQuestGiverId
                READ -> it.first.startQuestGiverId
                else -> null
            }
        }.distinct().toSet()
        return levelQuests.filter { it.startQuestGiverId !in notAvailableNpcs }
    }

    private fun addQuests(
        data: QuestDeclineRequestProcessData,
        levelQuests: List<RedisQuest>,
        toAdd: Int
    ): List<RedisUserQuestProgress> {
        val questsWithUniqueQuestGivers = levelQuests
            .shuffled(random)
            .distinctBy { it.startQuestGiverId }
        val quests =
            questsWithUniqueQuestGivers
                .shuffled(random)
                .take(min(toAdd, questsWithUniqueQuestGivers.size))
                .toMutableList()
        val userQuests = quests.map { quest ->
            RedisUserQuestProgress(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = quest.gameId,
                questId = quest.questId,
                userId = data.gameUser!!.userId,
            )
        }
        return redisUserQuestProgressRepository.saveAll(userQuests).toList()
    }

    private fun cleanUserQuestLog(
        finished: List<RedisUserQuestProgress>,
        declined: List<RedisUserQuestProgress>,
    ) {
        finished.forEach {
            it.questState = FINISHED_AVAILABLE
        }
        redisUserQuestProgressRepository.saveAll(finished)
        declined.forEach {
            it.questState = DECLINED_AVAILABLE
        }
        redisUserQuestProgressRepository.saveAll(declined)
    }
}