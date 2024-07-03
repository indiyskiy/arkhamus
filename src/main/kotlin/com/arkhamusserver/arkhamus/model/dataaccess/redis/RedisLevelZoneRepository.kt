package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelZoneRepository : CrudRepository<RedisLevelZone, String> {
    fun findByGameId(gameId: Long): List<RedisLevelZone>
    fun findByGameIdAndZoneType(gameId: Long, zoneType: ZoneType): List<RedisLevelZone>
}