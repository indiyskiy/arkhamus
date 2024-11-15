package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.MyCrudRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import org.springframework.stereotype.Repository

@Repository
class RedisShortTimeEventRepository : RamCrudRepository<RedisShortTimeEvent>()