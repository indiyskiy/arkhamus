package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisLanternRepository : CrudRepository<RedisLantern, String>,
    ToDeleteOnGameEnd<RedisLantern>,
    ToDeleteOnServerStart<RedisLantern>,
    CountInStatistic<RedisLantern> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.LANTERN
    override fun findByGameId(gameId: Long): List<RedisLantern>
}