package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRepository : CrudRepository<RedisQuest, String> {
    fun findByGameId(gameId: Long): List<RedisQuest>
}