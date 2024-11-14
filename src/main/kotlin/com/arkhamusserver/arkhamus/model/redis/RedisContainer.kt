package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisContainer")
data class RedisContainer(
    @Id var id: String,
    @Indexed var gameId: Long,
    var containerId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var z: Double,
    var containerTags: MutableSet<String> = mutableSetOf(),
    var interactionRadius: Double = 0.0,
    var items: MutableMap<Int, Int> = HashMap(),
    var gameTags: MutableSet<String> = mutableSetOf(),
    var visibilityModifiers: MutableSet<String>,
) : WithPoint, WithId, WithGameTags, WithVisibilityModifiers, Interactable {

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
        return containerId
    }

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}