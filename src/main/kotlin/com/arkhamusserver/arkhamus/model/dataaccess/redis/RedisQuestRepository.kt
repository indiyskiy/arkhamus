package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRepository : MyCrudRepository<RedisQuest> {
    override fun findByGameId(gameId: Long): List<RedisQuest>
}