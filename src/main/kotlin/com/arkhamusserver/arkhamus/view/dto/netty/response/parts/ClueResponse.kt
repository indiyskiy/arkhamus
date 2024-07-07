package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.GodType
import com.arkhamusserver.arkhamus.model.redis.RedisClue

data class ClueResponse(
    val zoneId: Long,
    val clue: GodType
) {
    constructor(redisClue: RedisClue) : this(redisClue.levelZoneId, redisClue.clue)
}