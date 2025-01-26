package com.arkhamusserver.arkhamus.model.redis.clues

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisScentClue(
    override val id: String,
    override val gameId: Long,
    val redisScentId: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val interactionRadius: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,
    var castedAbilityUsers: Set<Long> = setOf()
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
        return redisScentId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}