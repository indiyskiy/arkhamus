package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.springframework.stereotype.Repository

@Repository
interface RedisAbilityCastRepository : MyCrudRepository<RedisAbilityCast> {
    override fun findByGameId(gameId: Long): List<RedisAbilityCast>
}