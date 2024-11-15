package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import org.springframework.stereotype.Repository

@Repository
class RedisCrafterRepository : RamCrudRepository<RedisCrafter>()