package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Repository

@Repository
class RedisGameUserRepository : RamCrudRepository<RedisGameUser>() {
    fun findByUserIdAndGameId(userId: Long, gameId: Long): List<RedisGameUser> =
        map.values.filter { it.inGameId() == userId && it.gameId == gameId }

    fun findByUserId(userId: Long): List<RedisGameUser> =
        map.values.filter { it.inGameId() == userId }
}