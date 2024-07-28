package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisContainerRepository: CrudRepository<RedisContainer, String>,
    ToDeleteOnGameEnd<RedisContainer>,
    ToDeleteOnServerStart<RedisContainer>,
    CountInStatistic<RedisContainer> {
    override fun findByGameId(gameId: Long): List<RedisContainer>
}