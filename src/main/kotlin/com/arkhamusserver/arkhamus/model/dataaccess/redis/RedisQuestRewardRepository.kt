package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRewardRepository : CrudRepository<RedisQuestReward, String>,
    ToDeleteOnGameEnd<RedisQuestReward>,
    ToDeleteOnServerStart<RedisQuestReward>,
    CountInStatistic<RedisQuestReward> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.QUEST_REWARD
    override fun findByGameId(gameId: Long): List<RedisQuestReward>
}