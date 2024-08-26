package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisUserVoteSpot")
data class RedisUserVoteSpot(
    @Id var id: String,
    @Indexed var gameId: Long,
    var voteSpotId: Long,
    var userId: Long,
    var votes: MutableList<Long> = mutableListOf(),
)