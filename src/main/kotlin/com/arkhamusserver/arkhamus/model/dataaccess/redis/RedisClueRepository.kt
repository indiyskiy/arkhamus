package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisClue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisClueRepository : CrudRepository<RedisClue, String> {
    fun findByGameId(gameId: Long): List<RedisClue>
    fun findByGameIdAndLevelZoneId(gameId: Long, levelZoneId: Long): List<RedisClue>
}