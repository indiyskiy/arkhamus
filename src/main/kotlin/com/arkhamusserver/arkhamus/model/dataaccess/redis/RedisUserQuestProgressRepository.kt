package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import org.springframework.stereotype.Repository

@Repository
interface RedisUserQuestProgressRepository : MyCrudRepository<RedisUserQuestProgress> {
    override fun findByGameId(gameId: Long): List<RedisUserQuestProgress>
}