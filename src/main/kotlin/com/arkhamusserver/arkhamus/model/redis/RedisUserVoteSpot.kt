package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity

data class RedisUserVoteSpot(
    override var id: String,
    override var gameId: Long,
    var voteSpotId: Long,
    var userId: Long,
    var votesForUserIds: MutableList<Long> = mutableListOf(),
) : RedisGameEntity