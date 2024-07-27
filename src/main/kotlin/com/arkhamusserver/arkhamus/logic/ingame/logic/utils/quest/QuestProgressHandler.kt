package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import org.springframework.stereotype.Component

@Component
class QuestProgressHandler {
    fun filterQuestProgresses(
        questProgressByUserId: Map<Long, List<RedisUserQuestProgress>>,
        user: RedisGameUser
    ): List<RedisUserQuestProgress> {
        return questProgressByUserId[user.userId] ?: emptyList()
    }
}