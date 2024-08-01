package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import org.springframework.stereotype.Repository

@Repository
interface RedisClueRepository : MyCrudRepository<RedisClue> {
    override fun findByGameId(gameId: Long): List<RedisClue>
}