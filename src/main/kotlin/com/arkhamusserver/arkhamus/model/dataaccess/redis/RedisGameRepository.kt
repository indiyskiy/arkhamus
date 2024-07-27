package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGameRepository : CrudRepository<RedisGame, String> {
    fun redisResourceType(): RedisResourceType = RedisResourceType.GAME
    fun findByGameId(gameId: Long): RedisGame
}