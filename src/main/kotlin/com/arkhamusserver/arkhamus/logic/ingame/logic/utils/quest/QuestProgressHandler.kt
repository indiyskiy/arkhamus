package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import org.springframework.stereotype.Component

@Component
class QuestProgressHandler {
    fun mapQuestProgresses(
        questProgressByUserId: Map<Long, List<RedisUserQuestProgress>>,
        user: RedisGameUser,
        quests: List<RedisQuest>
    ): List<UserQuestResponse> {
        return (questProgressByUserId[user.userId] ?: emptyList()).map { userQuest ->
            val quest = quests.firstOrNull { it.questId == userQuest.questId }
            UserQuestResponse(
                id = userQuest.id,
                questId = userQuest.questId,
                questState = userQuest.questState,
                questCurrentStep = userQuest.questCurrentStep,
                questStepIds = quest?.levelTaskId ?: emptyList(),
                endQuestGiverId = quest?.endQuestGiverId,
                startQuestGiverId = quest?.startQuestGiverId,
            )
        }
    }
}