package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarRepository: CrudRepository<RedisAltar, String> {
    fun findByGameId(gameId: Long): List<RedisAltar>
    fun findByGameIdAndAltarId(gameId: Long, altarId: Long): List<RedisAltar>
}