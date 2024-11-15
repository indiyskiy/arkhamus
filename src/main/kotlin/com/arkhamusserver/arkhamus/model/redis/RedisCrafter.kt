package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.interfaces.*

data class RedisCrafter(
    override var id: String,
    override var gameId: Long,
    var crafterId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double = 0.0,
    var items: MutableMap<Int, Int> = HashMap(),
    var crafterType: CrafterType,
    var gameTags: MutableSet<String> = mutableSetOf(),
    var visibilityModifiers: MutableSet<String>,
) : RedisGameEntity, WithPoint, WithTrueIngameId, WithGameTags, WithVisibilityModifiers, Interactable {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun gameTags(): MutableSet<String> {
        return gameTags
    }

    override fun inGameId(): Long {
        return crafterId
    }

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}