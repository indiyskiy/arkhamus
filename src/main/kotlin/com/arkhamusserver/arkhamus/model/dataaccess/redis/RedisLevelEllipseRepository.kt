package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneEllipse
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelEllipseRepository : MyCrudRepository<RedisLevelZoneEllipse> {
    override fun findByGameId(gameId: Long): List<RedisLevelZoneEllipse>
}