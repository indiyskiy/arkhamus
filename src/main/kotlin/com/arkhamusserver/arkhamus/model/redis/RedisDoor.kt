package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisDoor")
data class RedisDoor(
    @Id var id: String,
    @Indexed var gameId: Long,
    var doorId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var zoneId: Long,
    var globalState: DoorState = DoorState.OPEN,
    var closedForUsers: MutableList<Long> = mutableListOf(),
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
        return doorId
    }


    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers.map { enumValueOf<VisibilityModifier>(it) }
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        visibilityModifiers = modifiers.map { it.name }.toMutableList()
    }
}