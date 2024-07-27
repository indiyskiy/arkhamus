package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRewardRepository : CrudRepository<RedisQuestReward, String> {
    fun findByGameId(gameId: Long): List<RedisQuestReward>
}