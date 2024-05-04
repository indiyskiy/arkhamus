package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisContainerRepository: CrudRepository<RedisContainer, String> {
    fun findByGameId(gameId: Long): List<RedisContainer>
    fun findByGameIdAndContainerId(gameId: Long, containerId: Long): List<RedisContainer>
}