package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLevelZone")
data class RedisLevelZone(
    @Id var id: String,
    @Indexed var gameId: Long,
    var levelZoneId: Long,
    var zoneType: ZoneType,
    @TimeToLive val timeToLive: Long = 10800
)