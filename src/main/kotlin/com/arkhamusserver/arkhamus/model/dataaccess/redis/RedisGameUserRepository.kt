package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.CountInStatistic
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnGameEnd
import com.arkhamusserver.arkhamus.model.dataaccess.ToDeleteOnServerStart
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.admin.RedisResourceType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGameUserRepository : CrudRepository<RedisGameUser, String>,
    ToDeleteOnGameEnd<RedisGameUser>,
    ToDeleteOnServerStart<RedisGameUser>,
    CountInStatistic<RedisGameUser> {
    override fun redisResourceType(): RedisResourceType = RedisResourceType.GAME_USER
    override fun findByGameId(gameId: Long): List<RedisGameUser>
    fun findByUserIdAndGameId(userId: Long, gameId: Long): List<RedisGameUser>
}