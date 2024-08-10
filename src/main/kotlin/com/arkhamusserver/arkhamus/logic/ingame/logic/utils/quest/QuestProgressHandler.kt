package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.TakeQuestRewardRequestProcessData
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

    fun finishQuest(
        quest: RedisQuest,
        globalGameData: GlobalGameData,
        data: TakeQuestRewardRequestProcessData
    ) {
        data.userQuestProgress?.let {
            it.questState = FINISHED
            questProgressRepository.save(it)
        }
        data.canAccept = false
        data.canDecline = false
        data.canFinish = false

        addMoreQuestsMaybe(globalGameData, data)
    }

    fun declineTheQuest(
        globalGameData: GlobalGameData,
        data: QuestDeclineRequestProcessData
    ) {
        data.userQuestProgress?.let {
            it.questState = DECLINED
            questProgressRepository.save(it)
        }
        data.canAccept = false
        data.canDecline = false
        data.canFinish = false

        addMoreQuestsMaybe(globalGameData, data)
    }

    private fun addMoreQuestsMaybe(
        globalGameData: GlobalGameData,
        data: GameUserData
    ) {
        logger.info("add more quests maybe?")
        val userQuestProgress = globalGameData.questProgressByUserId[data.gameUser!!.userId] ?: emptyList()
        if (userQuestCreationHandler.needToAddQuests(userQuestProgress)) {
            logger.info("add more quests definitely")
            val newQuestProgress = userQuestCreationHandler.addQuests(
                data,
                globalGameData.quests,
                userQuestProgress
            )
            logger.info("new quest progress ${newQuestProgress.size}")
            data.userQuest += newQuestProgress.map { mapQuestProgress(globalGameData.quests, it) }
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
                textKey = quest.textKey
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
                textKey = null
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

        val questSteps =
            globalGameData.questProgressByUserId[userId]?.filter { it.questState == IN_PROGRESS }
        val quests = globalGameData.quests
        val questIdToStep = questSteps?.map { it.questId to it.questCurrentStep }
        val questToStep = questIdToStep
            ?.map { quests.first { quest -> quest.questId == it.first } to it.second }
            ?.map { it.first to task(it.second, it.first.levelTaskIds) }
        val quest = questToStep?.firstOrNull { it.second == levelTaskId }?.first
        val userQuestProgress = quest?.let {
            questSteps.firstOrNull { questStep -> it.questId == questStep.questId }
        }

        return Pair(quest, userQuestProgress)
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

    private fun task(stepNumber: Int, levelTaskIds: MutableList<Long>): Long? {
        if (stepNumber < 0) {
            return null
        }
        if (stepNumber >= levelTaskIds.size) {
            return null
        }
        return levelTaskIds[stepNumber]
    }

    private fun questTaken(userQuest: RedisUserQuestProgress): Boolean {
        return userQuest.questState !in notTakenStates
    }

    private fun complete(userQuestProgress: RedisUserQuestProgress?) {
        userQuestProgress?.questState = COMPLETED
    }

}