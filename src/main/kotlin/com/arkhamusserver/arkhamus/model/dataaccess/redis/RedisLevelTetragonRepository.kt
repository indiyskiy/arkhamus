package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelTetragonRepository : CrudRepository<RedisLevelZoneTetragon, String> {
    fun findByGameId(gameId: Long): List<RedisLevelZoneTetragon>
    fun findByGameIdAndLevelZoneId(gameId: Long, levelZoneId: Long): List<RedisLevelZoneTetragon>
}