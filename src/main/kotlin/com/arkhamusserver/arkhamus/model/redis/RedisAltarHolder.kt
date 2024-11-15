package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId

data class RedisAltarHolder(
    override var id: String,
    override var gameId: Long,
    var altarHolderId: Long,

    var x: Double,
    var y: Double,
    var z: Double,
    var radius: Double = 0.0,

    var lockedGodId: Int? = null,
    var itemsForRitual: Map<Int, Int> = emptyMap(),
    var itemsIdToAltarId: Map<Int, Long> = emptyMap(),
    var itemsOnAltars: Map<Int, Int> = emptyMap(),

    var state: MapAltarState = MapAltarState.OPEN,
) : RedisGameEntity, WithPoint, WithTrueIngameId {

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
        return altarHolderId
    }
}