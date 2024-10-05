package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.redis.RedisClue

data class ClueResponse(
    val id: String,
    val zoneId: Long,
    val clue: Clue
) {
    constructor(redisClue: RedisClue) : this(
        redisClue.id,
        redisClue.levelZoneId,
        redisClue.clue
    )
}