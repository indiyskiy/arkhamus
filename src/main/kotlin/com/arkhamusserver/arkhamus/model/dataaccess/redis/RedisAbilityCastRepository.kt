package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAbilityCastRepository : CrudRepository<RedisAbilityCast, String> {
    fun findByGameId(gameId: Long): List<RedisAbilityCast>
}