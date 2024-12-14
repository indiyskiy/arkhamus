package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.redis.core.RedisHash

@RedisHash("RedisLantern")
data class RedisLantern(
    override var id: String,
    override var gameId: Long,
    var lanternId: Long,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var lanternState: LanternState,
    var fuel: Double = 0.0,
    var x: Double,
    var y: Double,
    var z: Double,
    var lightRange: Double,
    var interactionRadius: Double,
    var visibilityModifiers: Set<VisibilityModifier>,
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
        return lanternId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}