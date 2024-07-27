package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarRepository: CrudRepository<RedisAltar, String>,
    ToDeleteOnGameEnd<RedisAltar>,
    ToDeleteOnServerStart<RedisAltar>,
    CountInStatistic<RedisAltar> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.ALTAR
    override fun findByGameId(gameId: Long): List<RedisAltar>
}