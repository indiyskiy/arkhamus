package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarHolderRepository : MyCrudRepository<RedisAltarHolder> {
    override fun findByGameId(gameId: Long): List<RedisAltarHolder>
}