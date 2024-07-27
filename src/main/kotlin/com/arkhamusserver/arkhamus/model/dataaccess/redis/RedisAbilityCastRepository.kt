package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisAbilityCast
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAbilityCastRepository : CrudRepository<RedisAbilityCast, String>,
    ToDeleteOnGameEnd<RedisAbilityCast>,
    ToDeleteOnServerStart<RedisAbilityCast>,
    CountInStatistic<RedisAbilityCast> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.ABILITY_CAST
    override fun findByGameId(gameId: Long): List<RedisAbilityCast>
}