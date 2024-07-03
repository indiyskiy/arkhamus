package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLevelZone")
data class RedisLevelZone(
    @Id var id: String,
    @Indexed var gameId: Long,
    @Indexed var levelZoneId: Long,
    @Indexed var zoneType: ZoneType,
)