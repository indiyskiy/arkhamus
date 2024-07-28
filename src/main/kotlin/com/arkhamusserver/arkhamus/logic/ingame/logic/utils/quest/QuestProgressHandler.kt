package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import org.springframework.stereotype.Component

@Component
class QuestProgressHandler {

    companion object {
        val notTakenStates = setOf(UserQuestState.AWAITING)
    }

    fun mapQuestProgresses(
        questProgressByUserId: Map<Long, List<RedisUserQuestProgress>>,
        user: RedisGameUser,
        quests: List<RedisQuest>
    ): List<UserQuestResponse> {
        return (questProgressByUserId[user.userId] ?: emptyList()).map { userQuest ->
            val quest = quests.firstOrNull { it.questId == userQuest.questId }
            if (quest != null && questTaken(userQuest)) {
                UserQuestResponse(
                    id = userQuest.id,
                    questId = userQuest.questId,
                    questState = userQuest.questState,
                    questCurrentStep = userQuest.questCurrentStep,
                    questStepIds = quest.levelTaskId,
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
    }

    private fun questTaken(userQuest: RedisUserQuestProgress): Boolean {
        return userQuest.questState !in notTakenStates
    }

}