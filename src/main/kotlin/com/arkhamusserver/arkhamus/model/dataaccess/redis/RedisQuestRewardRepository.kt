package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRewardRepository : MyCrudRepository<RedisQuestReward> {
    override fun findByGameId(gameId: Long): List<RedisQuestReward>
}