package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarPollingRepository : CrudRepository<RedisAltarPolling, String> {
    fun findByGameId(gameId: Long): List<RedisAltarPolling>
}