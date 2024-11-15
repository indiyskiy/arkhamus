package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId

data class RedisLevelZone(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var zoneType: ZoneType,
) : RedisGameEntity, WithTrueIngameId {
    override fun inGameId(): Long {
        return levelZoneId
    }
}