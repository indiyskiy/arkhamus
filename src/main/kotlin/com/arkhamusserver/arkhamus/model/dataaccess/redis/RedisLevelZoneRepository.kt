package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelZoneRepository : CrudRepository<RedisLevelZone, String>,
    ToDeleteOnGameEnd<RedisLevelZone>,
    ToDeleteOnServerStart<RedisLevelZone>,
    CountInStatistic<RedisLevelZone> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.LEVEL_ZONE
    override fun findByGameId(gameId: Long): List<RedisLevelZone>
}