package com.arkhamusserver.arkhamus.model.redis.parts

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisSoundClueJammer(
    override val id: String,
    override val gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var inGameId: Long,
    var visibilityModifiers: Set<VisibilityModifier>,
    var interactionRadius: Double,
    var soundClueId: Long,
    var zoneId: Long,
    var turnedOn : Boolean
) : RedisGameEntity, WithPoint, WithTrueIngameId, WithVisibilityModifiers {
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
        return inGameId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}