package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisTimeEventRepository : CrudRepository<RedisTimeEvent, String> {
    fun findByGameId(gameId: Long): List<RedisTimeEvent>
}