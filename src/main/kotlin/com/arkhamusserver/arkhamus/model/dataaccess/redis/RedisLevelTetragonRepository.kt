package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLevelTetragonRepository : CrudRepository<RedisLevelZoneTetragon, String>,
    ToDeleteOnGameEnd<RedisLevelZoneTetragon>,
    ToDeleteOnServerStart<RedisLevelZoneTetragon>,
    CountInStatistic<RedisLevelZoneTetragon> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.TETRAGON
    override fun findByGameId(gameId: Long): List<RedisLevelZoneTetragon>
}