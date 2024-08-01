package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarPollingRepository : MyCrudRepository<RedisAltarPolling> {
    override fun findByGameId(gameId: Long): List<RedisAltarPolling>
}