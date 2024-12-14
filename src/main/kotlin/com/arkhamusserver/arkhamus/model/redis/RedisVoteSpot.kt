package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisVoteSpot(
    override var id: String,
    override var gameId: Long,
    var voteSpotId: Long,

    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double = 0.0,
    var zoneId: Long,
    var voteSpotState: VoteSpotState = VoteSpotState.WAITING_FOR_PAYMENT,
    var costValue: Int,
    var costItem: Item,
    var bannedUsers: MutableList<Long> = mutableListOf(),
    var availableUsers: MutableList<Long> = mutableListOf(),
    var visibilityModifiers: Set<VisibilityModifier>,
) : RedisGameEntity, WithPoint, WithTrueIngameId, WithVisibilityModifiers, Interactable {

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

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}