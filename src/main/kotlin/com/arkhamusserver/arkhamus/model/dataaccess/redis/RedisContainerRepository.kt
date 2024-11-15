package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import org.springframework.stereotype.Repository

@Repository
class RedisContainerRepository : RamCrudRepository<RedisContainer>()