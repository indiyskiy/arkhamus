package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisLantern")
data class RedisLantern(
    @Id var id: String,
    @Indexed var gameId: Long,
    var lanternId: Long,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var filled: Boolean = false,
    var activated: Boolean = false,
    var x: Double,
    var y: Double,
    var z: Double,
    var lightRange: Double,
    var visibilityModifiers: MutableList<String> = mutableListOf(),
) : WithPoint, WithId, WithVisibilityModifiers {

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
        return lanternId
    }

    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers.map { enumValueOf<VisibilityModifier>(it) }
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        visibilityModifiers = modifiers.map { it.name }.toMutableList()
    }
}