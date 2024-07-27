package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisQuestRepository : CrudRepository<RedisQuest, String>,
    ToDeleteOnGameEnd<RedisQuest>,
    ToDeleteOnServerStart<RedisQuest>,
    CountInStatistic<RedisQuest> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.QUEST
    override fun findByGameId(gameId: Long): List<RedisQuest>
}