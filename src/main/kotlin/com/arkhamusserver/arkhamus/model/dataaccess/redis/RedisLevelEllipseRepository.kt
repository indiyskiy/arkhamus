package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneEllipse
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelEllipseRepository : CrudRepository<RedisLevelZoneEllipse, String> {
    fun findByGameId(gameId: Long): List<RedisLevelZoneEllipse>
    fun findByGameIdAndLevelZoneId(gameId: Long, levelZoneId: Long): List<RedisLevelZoneEllipse>
}