package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisQuestGiver")
data class RedisQuestGiver(
    @Id
    var id: String,
    @Indexed var questGiverId: Long,
    @Indexed var gameId: Long,
    @Indexed var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var interactionRadius: Double,
    ) : WithPoint {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }
}