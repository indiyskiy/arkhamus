package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisQuest")
data class RedisQuest(
    @Id
    var id: String,
    var startQuestGiverId: Long,
    var endQuestGiverId: Long,
    @Indexed var gameId: Long,
    @Indexed var questId: Long,
    var levelTaskId: MutableList<Long> = mutableListOf(),
)