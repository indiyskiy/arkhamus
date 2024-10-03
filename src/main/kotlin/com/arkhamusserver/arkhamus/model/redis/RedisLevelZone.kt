package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLevelZone")
data class RedisLevelZone(
    @Id var id: String,
    @Indexed var gameId: Long,
    var levelZoneId: Long,
    var zoneType: ZoneType,
) : WithId {
    override fun inGameId(): Long {
        return levelZoneId
    }
}