package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisCrafter")
data class RedisCrafter(
    @Id var id: String,
    @Indexed var gameId: Long,
    var crafterId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var interactionRadius: Double = 0.0,
    var items: MutableMap<Int, Int> = HashMap(),
    var crafterType: CrafterType,
) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}