package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

data class RedisLevelZone(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var zoneType: ZoneType,
) : RedisGameEntity, WithId {
    override fun inGameId(): Long {
        return levelZoneId
    }
}