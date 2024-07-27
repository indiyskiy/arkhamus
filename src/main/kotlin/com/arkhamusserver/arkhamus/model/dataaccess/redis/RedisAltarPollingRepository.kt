package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarPollingRepository : CrudRepository<RedisAltarPolling, String>,
    ToDeleteOnGameEnd<RedisAltarPolling>,
    ToDeleteOnServerStart<RedisAltarPolling>,
    CountInStatistic<RedisAltarPolling> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.ALTAR_POLLING
    override fun findByGameId(gameId: Long): List<RedisAltarPolling>
}