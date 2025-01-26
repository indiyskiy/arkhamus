package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.logic.ingame.logic.visibility.VisibilityMap
import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity

class RedisVisibilityMap(
    override var id: String,
    override var gameId: Long,
    // TODO maybe get dir of this composition later
    val visibilityMap: VisibilityMap
) : RedisGameEntity