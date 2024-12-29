package com.arkhamusserver.arkhamus.model.dataaccess.redis.clues

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue
import org.springframework.stereotype.Repository

@Repository
class RedisScentClueRepository : RamCrudRepository<RedisScentClue>()