package com.arkhamusserver.arkhamus.model.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisVoteSpot")
data class RedisVoteSpot(
    @Id var id: String,
    @Indexed var gameId: Long,
    var voteSpotId: Long,

    var x: Double = 0.0,
    var y: Double = 0.0,
    var interactionRadius: Double = 0.0,
    var zoneId: Long,

    var costValue: Int? = null,
    var costItem: Int? = null,
    var bannedUsers: MutableList<Long> = mutableListOf(),
    var availableUsers: MutableList<Long> = mutableListOf(),
) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}