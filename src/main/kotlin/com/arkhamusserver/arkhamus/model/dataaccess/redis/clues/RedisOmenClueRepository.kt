package com.arkhamusserver.arkhamus.model.dataaccess.redis.clues

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.clues.RedisOmenClue
import org.springframework.stereotype.Repository

@Repository
class RedisOmenClueRepository : RamCrudRepository<RedisOmenClue>()