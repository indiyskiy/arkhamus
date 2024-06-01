package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarHolderRepository : CrudRepository<RedisAltarHolder, String> {
    fun findByGameId(gameId: Long): List<RedisAltarHolder>
}