package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisCrafterRepository: CrudRepository<RedisCrafter, String> {
    fun findByGameId(gameId: Long): List<RedisCrafter>
    fun findByGameIdAndCrafterId(gameId: Long, crafterId: Long): List<RedisCrafter>
}