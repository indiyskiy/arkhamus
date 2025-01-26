package com.arkhamusserver.arkhamus.model.redis.clues

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisOmenClue(
    override val id: String,
    override val gameId: Long,
    val redisOmenId: Long,
    val userId: Long,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,
    var castedAbilityUsers: Set<Long> = setOf(),
    var interactionRadius: Double = 0.0
) : RedisGameEntity, WithTrueIngameId, WithVisibilityModifiers {

    override fun inGameId(): Long {
        return redisOmenId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

}