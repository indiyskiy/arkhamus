package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Repository

@Repository
interface RedisLanternRepository : MyCrudRepository<RedisLantern> {
    override fun findByGameId(gameId: Long): List<RedisLantern>
}