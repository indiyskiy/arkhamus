package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGameUserRepository: CrudRepository<RedisGameUser, String> {
    fun findByGameId(gameId: Long): List<RedisGameUser>
}