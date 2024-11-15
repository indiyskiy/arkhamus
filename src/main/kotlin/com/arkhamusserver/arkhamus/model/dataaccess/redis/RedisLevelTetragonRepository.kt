package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import org.springframework.stereotype.Repository

@Repository
class RedisLevelTetragonRepository : RamCrudRepository<RedisLevelZoneTetragon>()