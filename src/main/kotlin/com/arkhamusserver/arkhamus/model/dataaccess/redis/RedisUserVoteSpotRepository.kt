package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import org.springframework.stereotype.Repository

@Repository
interface RedisUserVoteSpotRepository : MyCrudRepository<RedisUserVoteSpot> {
    override fun findByGameId(gameId: Long): List<RedisUserVoteSpot>
}