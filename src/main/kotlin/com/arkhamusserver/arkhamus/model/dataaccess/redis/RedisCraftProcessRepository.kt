package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisCraftProcess
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisCraftProcessRepository : CrudRepository<RedisCraftProcess, String>,
    ToDeleteOnGameEnd<RedisCraftProcess>,
    ToDeleteOnServerStart<RedisCraftProcess>,
    CountInStatistic<RedisCraftProcess> {
    override fun findByGameId(gameId: Long): List<RedisCraftProcess>
}