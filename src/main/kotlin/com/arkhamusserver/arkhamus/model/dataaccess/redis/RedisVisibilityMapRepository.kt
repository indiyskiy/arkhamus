package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisVisibilityMap
import org.springframework.stereotype.Repository

@Repository
class RedisVisibilityMapRepository: RamCrudRepository<RedisVisibilityMap>()