package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisAltar(
    override var id: String,
    var altarId: Long,
    override var gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double,
    var visibilityModifiers: MutableSet<String>,
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
        return altarId
    }

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}