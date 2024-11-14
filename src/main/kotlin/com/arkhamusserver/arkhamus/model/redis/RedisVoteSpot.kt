package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisVoteSpot")
data class RedisVoteSpot(
    @Id var id: String,
    @Indexed var gameId: Long,
    var voteSpotId: Long,

    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double = 0.0,
    var zoneId: Long,
    var voteSpotState: VoteSpotState = VoteSpotState.WAITING_FOR_PAYMENT,
    var costValue: Int? = null,
    var costItem: Int? = null,
    var bannedUsers: MutableList<Long> = mutableListOf(),
    var availableUsers: MutableList<Long> = mutableListOf(),
    var visibilityModifiers: MutableSet<String>,
) : WithPoint, WithId, WithVisibilityModifiers, Interactable {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun inGameId(): Long {
        return voteSpotId
    }
    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}