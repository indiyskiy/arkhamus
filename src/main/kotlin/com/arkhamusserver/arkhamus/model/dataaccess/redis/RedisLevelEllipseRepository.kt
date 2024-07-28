package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneEllipse
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelEllipseRepository : CrudRepository<RedisLevelZoneEllipse, String>,
    ToDeleteOnGameEnd<RedisLevelZoneEllipse>,
    ToDeleteOnServerStart<RedisLevelZoneEllipse>,
    CountInStatistic<RedisLevelZoneEllipse> {
    override fun findByGameId(gameId: Long): List<RedisLevelZoneEllipse>
}