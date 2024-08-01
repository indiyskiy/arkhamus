package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import org.springframework.stereotype.Repository

@Repository
interface RedisCraftProcessRepository : MyCrudRepository<RedisCraftProcess> {
    override fun findByGameId(gameId: Long): List<RedisCraftProcess>
}