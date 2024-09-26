package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_TO_REFRESH
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class UserQuestCreationHandler(
    private val redisUserQuestProgressRepository: RedisUserQuestProgressRepository,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(UserQuestCreationHandler::class.java)
        val random: Random = Random(System.currentTimeMillis())

        val QUESTS_IN_PROGRESS = setOf(
            AWAITING,
            READ,
            IN_PROGRESS,
            COMPLETED
        )
        val QUESTS_RELEVANT = setOf(
            AWAITING,
            READ,
            IN_PROGRESS,
            DECLINED,
            COMPLETED,
            FINISHED,
        )

        val ON_DELETE = setOf(
            DECLINED,
            FINISHED,
        )
    }

    fun needToAddQuests(
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): Boolean {
        val userInProgress = userQuestsProgresses
            .count {
                it.questState in QUESTS_IN_PROGRESS
            }
        logger.info("add more quests maybe? $userInProgress < $QUESTS_TO_REFRESH")
        return userInProgress <= QUESTS_TO_REFRESH
    }

    @Transactional
    fun addQuests(
        data: GameUserData,
        levelQuests: List<RedisQuest>,
        userQuestsProgresses: List<RedisUserQuestProgress>,
        currentGameTime: Long,
    ): List<RedisUserQuestProgress> {
        val questsToAdd = questsToAdd(userQuestsProgresses)
        logger.info("quests to add $questsToAdd")
        val availableQuests = availableQuests(levelQuests, userQuestsProgresses)
        if (availableQuests.map { it.startQuestGiverId }.distinct().size >= questsToAdd) {
            return addQuests(
                data,
                questsToAdd,
                availableQuests,
                currentGameTime
            )
        } else {
            val cleanedUpQuests = cleanOldQuests(levelQuests, userQuestsProgresses)
            val cleanedUpQuestsAvailableByQuestGiver = filterByNpc(userQuestsProgresses, cleanedUpQuests)
            val newAvailableQuests = availableQuests + cleanedUpQuestsAvailableByQuestGiver
            return addQuests(data, questsToAdd, newAvailableQuests, currentGameTime)
        }
    }

    fun setStartsQuestsForUser(
        user: RedisGameUser,
        createdRedisQuests: List<RedisQuest>
    ) {
        val quests = getQuestsWithUniqueQuestGivers(createdRedisQuests)
            .take(QUESTS_ON_START)
            .toMutableList()
        addQuestsForUser(quests, user, 0)
    }

    private fun cleanOldQuests(
        levelQuests: List<RedisQuest>,
        userQuestsProgresses: List<RedisUserQuestProgress>,
    ): List<RedisQuest> {
        logger.info("clean up old quests")
        val questToDelete = userQuestsProgresses.filter {
            it.questState in ON_DELETE
        }
        logger.info("quests to delete ${questToDelete.joinToString { it.questId.toString() }}")
        questToDelete.forEach {
            when (it.questState) {
                FINISHED -> {
                    it.questState = FINISHED_AVAILABLE
                }

                DECLINED -> {
                    it.questState = DECLINED_AVAILABLE
                }

                else -> {}
            }
        }
        val questToDeleteIds = questToDelete.map { it.questId }.toSet()
        val newlyAvailableQuests = levelQuests.filter { it.questId in questToDeleteIds }
        logger.info("newly available quests ${newlyAvailableQuests.joinToString { it.questId.toString() }}")
        return newlyAvailableQuests
    }

    private fun filterByNpc(
        userQuestsProgresses: List<RedisUserQuestProgress>,
        levelQuests: List<RedisQuest>
    ): List<RedisQuest> {
        val inProgress = userQuestsProgresses
            .filter {
                it.questState in QUESTS_IN_PROGRESS
            }
        val inProgressQuestGivers = inProgress.mapNotNull { userQuest ->
            levelQuests.firstOrNull {
                userQuest.questId == it.questId
            }
        }.map {
            it.startQuestGiverId
        }
        val notRelevantFreeByQuestGivers = levelQuests.filter {
            it.startQuestGiverId !in inProgressQuestGivers
        }
        return notRelevantFreeByQuestGivers
    }

    private fun availableQuests(
        levelQuests: List<RedisQuest>,
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): List<RedisQuest> {
        val relevant = userQuestsProgresses
            .filter {
                it.questState in QUESTS_RELEVANT
            }
        val relevantIds = relevant.map { it.questId }.toSet()
        logger.info("relevant quests ${relevantIds.joinToString(",") { it.toString() }}")

        val notRelevant = levelQuests.filter { it.questId !in relevantIds }
        logger.info("not relevant quests ${notRelevant.joinToString(",") { it.questId.toString() }}")

        val inProgress = userQuestsProgresses
            .filter {
                it.questState in QUESTS_IN_PROGRESS
            }
        logger.info("in progress quests ${inProgress.joinToString(",") { it.questId.toString() }}")

        val inProgressQuestGivers = inProgress.map { userQuest ->
            levelQuests.first {
                userQuest.questId == it.questId
            }
        }.map {
            it.startQuestGiverId
        }
        logger.info("in progress quests givers ${inProgressQuestGivers.joinToString(",") { it.toString() }}")

        val notRelevantFreeByQuestGivers = notRelevant.filter {
            it.startQuestGiverId !in inProgressQuestGivers
        }
        logger.info("not relevant not blocked by quest givers ${notRelevantFreeByQuestGivers.joinToString(",") { it.questId.toString() }}")

        return notRelevantFreeByQuestGivers
    }

    private fun questsToAdd(
        userQuestsProgresses: List<RedisUserQuestProgress>
    ): Int {
        val userInProgress = userQuestsProgresses
            .count {
                it.questState in QUESTS_IN_PROGRESS
            }
        return QUESTS_ON_START - userInProgress
    }

    private fun addQuestsForUser(
        quests: List<RedisQuest>,
        user: RedisGameUser,
        currentGameTime: Long
    ): List<RedisUserQuestProgress> {
        val userStartQuests = quests.map { quest ->
            RedisUserQuestProgress(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = quest.gameId,
                questId = quest.questId,
                userId = user.userId,
                creationGameTime = currentGameTime
            )
        }
        return redisUserQuestProgressRepository.saveAll(userStartQuests).toList()
    }

    private fun addQuests(
        data: GameUserData,
        questsToAddSize: Int,
        availableQuests: List<RedisQuest>,
        currentGameTime: Long
    ): List<RedisUserQuestProgress> {
        val questsToAdd = getQuestsWithUniqueQuestGivers(availableQuests).take(questsToAddSize)
        return addQuestsForUser(
            questsToAdd,
            data.gameUser!!,
            currentGameTime
        )
    }

    private fun getQuestsWithUniqueQuestGivers(createdRedisQuests: List<RedisQuest>): List<RedisQuest> =
        createdRedisQuests
            .groupBy { it.startQuestGiverId }
            .map { it.value.random(random) }
            .shuffled(random)

}