package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.stereotype.Repository

@Repository
interface RedisContainerRepository : MyCrudRepository<RedisContainer> {
    override fun findByGameId(gameId: Long): List<RedisContainer>
}