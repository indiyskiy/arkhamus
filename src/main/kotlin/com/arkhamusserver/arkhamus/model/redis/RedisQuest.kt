package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisQuest")
data class RedisQuest(
    @Id
    var id: String,
    @Indexed var gameId: Long,
    var questId: Long,
    var startQuestGiverId: Long,
    var endQuestGiverId: Long,
    var difficulty: QuestDifficulty,
    var levelTaskIds: MutableList<Long> = mutableListOf(),
    var textKey: String,
) : WithId {
    override fun inGameId(): Long {
        return questId
    }
}