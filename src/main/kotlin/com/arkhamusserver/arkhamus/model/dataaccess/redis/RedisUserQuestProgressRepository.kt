package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisUserQuestProgressRepository : CrudRepository<RedisUserQuestProgress, String>,
    ToDeleteOnGameEnd<RedisUserQuestProgress>,
    ToDeleteOnServerStart<RedisUserQuestProgress>,
    CountInStatistic<RedisUserQuestProgress> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.USER_QUEST_PROGRESS
    override fun findByGameId(gameId: Long): List<RedisUserQuestProgress>
}