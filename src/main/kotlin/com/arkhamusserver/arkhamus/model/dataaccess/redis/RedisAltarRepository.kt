package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import org.springframework.stereotype.Repository

@Repository
class RedisAltarRepository : RamCrudRepository<RedisAltar>()