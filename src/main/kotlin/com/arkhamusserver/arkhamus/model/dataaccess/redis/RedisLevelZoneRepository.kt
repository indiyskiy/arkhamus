package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.stereotype.Repository

@Repository
class RedisLevelZoneRepository : RamCrudRepository<RedisLevelZone>()