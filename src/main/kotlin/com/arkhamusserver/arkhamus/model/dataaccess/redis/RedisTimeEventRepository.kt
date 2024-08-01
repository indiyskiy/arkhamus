package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Repository

@Repository
interface RedisTimeEventRepository : MyCrudRepository<RedisTimeEvent> {
    override fun findByGameId(gameId: Long): List<RedisTimeEvent>
}