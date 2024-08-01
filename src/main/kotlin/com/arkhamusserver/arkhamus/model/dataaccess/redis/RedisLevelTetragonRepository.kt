package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelTetragonRepository : MyCrudRepository<RedisLevelZoneTetragon> {
    override fun findByGameId(gameId: Long): List<RedisLevelZoneTetragon>
}