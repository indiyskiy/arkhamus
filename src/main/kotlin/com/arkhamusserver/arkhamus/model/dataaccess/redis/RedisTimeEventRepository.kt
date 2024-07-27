package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisTimeEventRepository : CrudRepository<RedisTimeEvent, String>,
    ToDeleteOnGameEnd<RedisTimeEvent>,
    ToDeleteOnServerStart<RedisTimeEvent>,
    CountInStatistic<RedisTimeEvent> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.TIME_EVENT
    override fun findByGameId(gameId: Long): List<RedisTimeEvent>
}