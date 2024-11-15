package com.arkhamusserver.arkhamus.model.dataaccess.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import org.springframework.stereotype.Repository

@Repository
class RedisUserQuestProgressRepository : RamCrudRepository<RedisUserQuestProgress>()