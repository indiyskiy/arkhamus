package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisAltar")
data class RedisAltar(
    @Id var id: String,
    var altarId: Long,
    @Indexed var gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double,
) : WithPoint, WithId {

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
        return altarId
    }
}