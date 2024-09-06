package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisThreshold
import org.springframework.stereotype.Repository

@Repository
interface RedisThresholdRepository : MyCrudRepository<RedisThreshold> {
    override fun findByGameId(gameId: Long): List<RedisThreshold>
}