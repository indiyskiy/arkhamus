package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisCrafterRepository: CrudRepository<RedisCrafter, String>,
    ToDeleteOnGameEnd<RedisCrafter>,
    ToDeleteOnServerStart<RedisCrafter>,
    CountInStatistic<RedisCrafter> {
    override fun findByGameId(gameId: Long): List<RedisCrafter>
}