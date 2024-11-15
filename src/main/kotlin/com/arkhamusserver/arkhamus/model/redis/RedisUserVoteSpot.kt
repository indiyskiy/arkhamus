package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

data class RedisUserVoteSpot(
    override var id: String,
    override var gameId: Long,
    var voteSpotId: Long,
    var userId: Long,
    var votesForUserIds: MutableList<Long> = mutableListOf(),
): RedisGameEntity