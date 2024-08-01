package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarRepository : MyCrudRepository<RedisAltar> {
    override fun findByGameId(gameId: Long): List<RedisAltar>
}