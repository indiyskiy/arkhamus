package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestDeclineRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import org.springframework.stereotype.Component

@Component
class QuestProgressHandler(
    private val questProgressRepository: RedisUserQuestProgressRepository,
    private val userQuestCreationHandler: UserQuestCreationHandler
) {

    companion object {
        val notTakenStates = setOf(UserQuestState.AWAITING)
    }

    fun acceptTheQuest(userQuestProgress: RedisUserQuestProgress?, data: QuestAcceptRequestProcessData) {
        userQuestProgress?.let {
            it.questState = UserQuestState.IN_PROGRESS
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
            it.questState = UserQuestState.DECLINED
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
            UserQuestState.AWAITING,
            UserQuestState.READ
        )

    fun canDecline(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            UserQuestState.AWAITING,
            UserQuestState.READ,
            UserQuestState.IN_PROGRESS
        )

    fun canFinish(quest: RedisQuest?, userQuestProgress: RedisUserQuestProgress?): Boolean =
        quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questState in setOf(
            UserQuestState.IN_PROGRESS
        ) && userQuestProgress.questCurrentStep == quest.levelTaskIds.size

    fun readTheQuest(userQuestProgress: RedisUserQuestProgress?) {
        userQuestProgress?.let {
            it.questState = UserQuestState.READ
            questProgressRepository.save(it)
        }
    }

    private fun questTaken(userQuest: RedisUserQuestProgress): Boolean {
        return userQuest.questState !in notTakenStates
    }

}