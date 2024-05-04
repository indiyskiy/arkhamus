package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLanternRepository: CrudRepository<RedisLantern, String> {
    fun findByGameId(gameId: Long): List<RedisLantern>
    fun findByGameIdAndLanternId(gameId: Long, lanternId: Long): List<RedisLantern>
}