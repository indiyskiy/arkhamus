package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import org.springframework.stereotype.Repository

@Repository
interface RedisShortTimeEventRepository : MyCrudRepository<RedisShortTimeEvent> {
    override fun findByGameId(gameId: Long): List<RedisShortTimeEvent>
}