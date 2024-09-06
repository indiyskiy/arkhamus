package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisDoor
import org.springframework.stereotype.Repository

@Repository
interface RedisDoorRepository : MyCrudRepository<RedisDoor> {
    override fun findByGameId(gameId: Long): List<RedisDoor>
}