package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import org.springframework.stereotype.Repository

@Repository
interface RedisCrafterRepository : MyCrudRepository<RedisCrafter> {
    override fun findByGameId(gameId: Long): List<RedisCrafter>
}