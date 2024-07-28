package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisAltarHolderRepository : CrudRepository<RedisAltarHolder, String>,
    ToDeleteOnGameEnd<RedisAltarHolder>,
    ToDeleteOnServerStart<RedisAltarHolder>,
    CountInStatistic<RedisAltarHolder> {
    override fun findByGameId(gameId: Long): List<RedisAltarHolder>
}