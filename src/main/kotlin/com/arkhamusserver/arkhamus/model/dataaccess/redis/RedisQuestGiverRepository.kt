package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisQuestGiver
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestGiverRepository : MyCrudRepository<RedisQuestGiver> {
    override fun findByGameId(gameId: Long): List<RedisQuestGiver>
}