package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisClueRepository : CrudRepository<RedisClue, String>,
    ToDeleteOnGameEnd<RedisClue>,
    ToDeleteOnServerStart<RedisClue>,
    CountInStatistic<RedisClue> {
    override fun findByGameId(gameId: Long): List<RedisClue>
}