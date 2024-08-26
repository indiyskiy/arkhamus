package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.springframework.stereotype.Repository

@Repository
interface RedisVoteSpotRepository : MyCrudRepository<RedisVoteSpot> {
    override fun findByGameId(gameId: Long): List<RedisVoteSpot>
}