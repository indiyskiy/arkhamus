package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisCraftProcessRepository : CrudRepository<RedisCraftProcess, String> {
    fun findByGameId(gameId: Long): List<RedisCraftProcess>
}