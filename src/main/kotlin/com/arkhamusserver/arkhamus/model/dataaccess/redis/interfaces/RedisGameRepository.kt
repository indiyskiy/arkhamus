package com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces

import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
class RedisGameRepository : RamCrudRepository<RedisGame>()