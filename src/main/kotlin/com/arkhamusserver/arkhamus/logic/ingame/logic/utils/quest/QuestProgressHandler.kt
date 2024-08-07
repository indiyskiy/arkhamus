package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class QuestProgressHandler(
    private val questProgressRepository: RedisUserQuestProgressRepository,
    private val userQuestCreationHandler: UserQuestCreationHandler
) {

    companion object {
        val notTakenStates = setOf(AWAITING)
        var logger: Logger = LoggerFactory.getLogger(QuestProgressHandler::class.java)
    }

    fun acceptTheQuest(userQuestProgress: RedisUserQuestProgress?, data: QuestAcceptRequestProcessData) {
        userQuestProgress?.let {
            it.questState = IN_PROGRESS
            it.questCurrentStep = 0
            questProgressRepository.save(it)
        }
        data.canAccept = false
        data.canDecline = true
    }

    fun declineTheQuest(
        globalGameData: GlobalGameData,
        data: QuestDeclineRequestProcessData
    ) {
        data.userQuestProgress?.let {
            it.questState = DECLINED
            it.questCurrentStep = 0
            questProgressRepository.save(it)
        }
        data.canAccept = false
        data.canDecline = false
        data.canFinish = false

        val userQuestProgress = globalGameData.questProgressByUserId[data.gameUser!!.userId] ?: emptyList()
        if (userQuestCreationHandler.needToAddQuests(userQuestProgress)) {
            val newQuestProgress = userQuestCreationHandler.addQuests(
                data, globalGameData.quests,
                userQuestProgress
            )
            data.userQuest = newQuestProgress.map { mapQuestProgress(globalGameData.quests, it) }
        }
    }

    fun mapQuestProgresses(
        questProgressByUserId: Map<Long, List<RedisUserQuestProgress>>,
        user: RedisGameUser,
        quests: List<RedisQuest>
    ): List<UserQuestResponse> {
        return (questProgressByUserId[user.userId] ?: emptyList()).map { userQuest ->
            mapQuestProgress(quests, userQuest)
        }
    }

    fun mapQuestProgress(
        quests: List<RedisQuest>,
        userQuest: RedisUserQuestProgress
    ): UserQuestResponse {
        val quest = quests.firstOrNull { it.questId == userQuest.questId }
        return mapQuestProgress(quest, userQuest)
    }

    fun mapQuestProgress(
        quest: RedisQuest?,
        userQuest: RedisUserQuestProgress,
    ): UserQuestResponse {
        return if (quest != null && questTaken(userQuest)) {
            UserQuestResponse(
                id = userQuest.id,
                questId = userQuest.questId,
                questState = userQuest.questState,
                questCurrentStep = userQuest.questCurrentStep,
                questStepIds = quest.levelTaskIds,
                endQuestGiverId = quest.endQuestGiverId,
                startQuestGiverId = quest.startQuestGiverId,
            )
        } else {
            UserQuestResponse(
                id = userQuest.id,
                questId = null,
                questState = userQuest.questState,
                questCurrentStep = userQuest.questCurrentStep,
                questStepIds = emptyList(),
                endQuestGiverId = null,
                startQuestGiverId = quest?.startQuestGiverId,
            )
        }
    }

    fun canAccept(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            AWAITING,
            READ
        )

    fun canDecline(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            AWAITING,
            READ,
            IN_PROGRESS
        )

    fun isCompleted(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(IN_PROGRESS, COMPLETED) &&
                userQuestProgress.questCurrentStep == quest.levelTaskIds.size

    fun canFinish(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(COMPLETED) &&
                userQuestProgress.questCurrentStep == quest.levelTaskIds.size

    fun readTheQuest(userQuestProgress: RedisUserQuestProgress?) {
        userQuestProgress?.let {
            it.questState = READ
            questProgressRepository.save(it)
        }
    }

    fun questAndProgress(
        levelTaskId: Long,
        globalGameData: GlobalGameData,
        userId: Long?
    ): Pair<RedisQuest?, RedisUserQuestProgress?> {
        logger.info("looking for a quest and user progress for $levelTaskId")

        val questSteps =
            globalGameData.questProgressByUserId[userId]?.filter { it.questState == IN_PROGRESS }
        logger.info("current questSteps ${questSteps?.joinToString { it.questId.toString() } ?: "-"}")

        val quests = globalGameData.quests
        logger.info("quests ${quests.joinToString { it.questId.toString() }}")

        val questIdToStep = questSteps?.map { it.questId to it.questCurrentStep }
        logger.info("questIdToStep ${questIdToStep?.joinToString { it.first.toString() + "/" + it.second }}")

        val questToStep = questIdToStep
            ?.map { quests.first { quest -> quest.questId == it.first } to it.second }
            ?.map { it.first to task(it.second, it.first.levelTaskIds) }
        logger.info("questToStep ${questToStep?.joinToString { it.first.toString() + "/" + it.second }}")

        val quest = questToStep?.firstOrNull { it.second == levelTaskId }?.first
        logger.info("quest ${quest?.questId}")

        val userQuestProgress = quest?.let {
            questSteps.firstOrNull { questStep -> it.questId == questStep.questId }
        }
        logger.info("userQuestProgress ${userQuestProgress?.questId}")

        return Pair(quest, userQuestProgress)
    }

    private fun task(stepNumber: Int, levelTaskIds: MutableList<Long>): Long? {
        if (stepNumber < 0) {
            logger.info("still on quest giver state")
            return null
        }
        if (stepNumber >= levelTaskIds.size) {
            logger.info("quest complete")
            return null
        }
        return levelTaskIds[stepNumber]
    }

    private fun questTaken(userQuest: RedisUserQuestProgress): Boolean {
        return userQuest.questState !in notTakenStates
    }

    fun nextStep(userQuestProgress: RedisUserQuestProgress?, quest: RedisQuest?) {
        userQuestProgress?.let { progress ->
            quest?.let { questNotNull ->
                progress.questCurrentStep = min(progress.questCurrentStep + 1, questNotNull.levelTaskIds.size)
                if (isCompleted(quest, userQuestProgress)) {
                    complete(userQuestProgress)
                }
                questProgressRepository.save(progress)
            }
        }
    }

    private fun complete(userQuestProgress: RedisUserQuestProgress?) {
        userQuestProgress?.questState = COMPLETED
    }

}