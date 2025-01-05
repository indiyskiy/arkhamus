package com.arkhamusserver.arkhamus.model.redis.clues

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import com.arkhamusserver.arkhamus.model.redis.parts.RedisSoundClueJammer

data class RedisSoundClue(
    override val id: String,
    override val gameId: Long,
    val redisSoundId: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,
    var zoneId: Long,
    var soundClueJammers: List<RedisSoundClueJammer>
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
        return redisSoundId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}