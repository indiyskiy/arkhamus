package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
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
    var lanternState: LanternState,
    var fuel: Double = 0.0,
    var x: Double,
    var y: Double,
    var z: Double,
    var lightRange: Double,
    var interactionRadius: Double,
    var visibilityModifiers: MutableSet<String>,
) : WithPoint, WithId, WithVisibilityModifiers, Interactable {

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

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}