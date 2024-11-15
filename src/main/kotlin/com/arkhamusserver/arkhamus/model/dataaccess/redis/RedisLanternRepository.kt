package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Repository

@Repository
class RedisLanternRepository : RamCrudRepository<RedisLantern>()