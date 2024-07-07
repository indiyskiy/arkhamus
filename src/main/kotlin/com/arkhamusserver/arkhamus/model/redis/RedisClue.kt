package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.GodType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisClue")
data class RedisClue(
    @Id
    var id: String,
    @Indexed
    var gameId: Long,
    @Indexed
    var levelZoneId: Long,
    var clue: GodType,
)