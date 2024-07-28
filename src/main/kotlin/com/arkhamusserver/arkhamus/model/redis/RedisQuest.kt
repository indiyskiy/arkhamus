package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisQuest")
data class RedisQuest(
    @Id
    var id: String,
    var startQuestGiverId: Long,
    var endQuestGiverId: Long,
    var difficulty: QuestDifficulty,
    @Indexed var gameId: Long,
    var questId: Long,
    var levelTaskIds: MutableList<Long> = mutableListOf(),
)