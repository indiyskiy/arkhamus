package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_TO_REFRESH
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState.*
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
import com.arkhamusserver.arkhamus.model.ingame.InGameUserQuestProgress
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class UserQuestCreationHandler(
    private val inGameUserQuestProgressRepository: InGameUserQuestProgressRepository,
) {
    companion object {
        private val logger = LoggingUtils.getLogger<UserQuestCreationHandler>()
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
        userQuestsProgresses: List<InGameUserQuestProgress>
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
        levelQuests: List<InGameQuest>,
        userQuestsProgresses: List<InGameUserQuestProgress>,
        currentGameTime: Long,
    ): List<InGameUserQuestProgress> {
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
        user: InGameUser,
        createdInGameQuests: List<InGameQuest>
    ) {
        val quests = getQuestsWithUniqueQuestGivers(createdInGameQuests)
            .take(QUESTS_ON_START)
            .toMutableList()
        addQuestsForUser(quests, user, 0)
    }

    private fun cleanOldQuests(
        levelQuests: List<InGameQuest>,
        userQuestsProgresses: List<InGameUserQuestProgress>,
    ): List<InGameQuest> {
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
        inGameUserQuestProgressRepository.deleteAll(questToDelete)
        val questToDeleteIds = questToDelete.map { it.questId }.toSet()
        val newlyAvailableQuests = levelQuests.filter { it.inGameId() in questToDeleteIds }
        logger.info("newly available quests ${newlyAvailableQuests.joinToString { it.questId.toString() }}")
        return newlyAvailableQuests
    }

    private fun filterByNpc(
        userQuestsProgresses: List<InGameUserQuestProgress>,
        levelQuests: List<InGameQuest>
    ): List<InGameQuest> {
        val inProgress = userQuestsProgresses
            .filter {
                it.questState in QUESTS_IN_PROGRESS
            }
        val inProgressQuestGivers = inProgress.mapNotNull { userQuest ->
            levelQuests.firstOrNull {
                userQuest.questId == it.inGameId()
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
        levelQuests: List<InGameQuest>,
        userQuestsProgresses: List<InGameUserQuestProgress>
    ): List<InGameQuest> {
        val relevant = userQuestsProgresses
            .filter {
                it.questState in QUESTS_RELEVANT
            }
        val relevantIds = relevant.map { it.questId }.toSet()
        logger.info("relevant quests ${relevantIds.joinToString(",") { it.toString() }}")

        val notRelevant = levelQuests.filter { it.inGameId() !in relevantIds }
        logger.info("not relevant quests ${notRelevant.joinToString(",") { it.questId.toString() }}")

        val inProgress = userQuestsProgresses
            .filter {
                it.questState in QUESTS_IN_PROGRESS
            }
        logger.info("in progress quests ${inProgress.joinToString(",") { it.questId.toString() }}")

        val inProgressQuestGivers = inProgress.map { userQuest ->
            levelQuests.first {
                userQuest.questId == it.inGameId()
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
        userQuestsProgresses: List<InGameUserQuestProgress>
    ): Int {
        val userInProgress = userQuestsProgresses
            .count {
                it.questState in QUESTS_IN_PROGRESS
            }
        return QUESTS_ON_START - userInProgress
    }

    private fun addQuestsForUser(
        quests: List<InGameQuest>,
        user: InGameUser,
        currentGameTime: Long
    ): List<InGameUserQuestProgress> {
        val userStartQuests = quests.map { quest ->
            InGameUserQuestProgress(
                id = generateRandomId(),
                gameId = quest.gameId,
                questId = quest.inGameId(),
                userId = user.inGameId(),
                creationGameTime = currentGameTime
            )
        }
        return inGameUserQuestProgressRepository.saveAll(userStartQuests).toList()
    }

    private fun addQuests(
        data: GameUserData,
        questsToAddSize: Int,
        availableQuests: List<InGameQuest>,
        currentGameTime: Long
    ): List<InGameUserQuestProgress> {
        val questsToAdd = getQuestsWithUniqueQuestGivers(availableQuests).take(questsToAddSize)
        return addQuestsForUser(
            questsToAdd,
            data.gameUser!!,
            currentGameTime
        )
    }

    private fun getQuestsWithUniqueQuestGivers(createdInGameQuests: List<InGameQuest>): List<InGameQuest> =
        createdInGameQuests
            .groupBy { it.startQuestGiverId }
            .map { it.value.random(random) }
            .shuffled(random)

}