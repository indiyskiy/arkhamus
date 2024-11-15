package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.springframework.stereotype.Repository

@Repository
class RedisVoteSpotRepository : RamCrudRepository<RedisVoteSpot>()