package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelZoneRepository : MyCrudRepository<RedisLevelZone> {
    override fun findByGameId(gameId: Long): List<RedisLevelZone>
}