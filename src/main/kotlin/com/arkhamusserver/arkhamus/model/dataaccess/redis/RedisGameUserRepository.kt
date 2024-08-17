package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Repository

@Repository
interface RedisGameUserRepository : MyCrudRepository<RedisGameUser> {
    override fun findByGameId(gameId: Long): List<RedisGameUser>
    fun findByUserIdAndGameId(userId: Long, gameId: Long): List<RedisGameUser>
    fun findByUserId(userId: Long): List<RedisGameUser>
}