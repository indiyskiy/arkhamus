package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import org.springframework.stereotype.Repository

@Repository
class RedisAltarHolderRepository : RamCrudRepository<RedisAltarHolder>()