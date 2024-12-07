package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
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

    var lockedGod: God? = null,
    var itemsForRitual: Map<Item, Int> = emptyMap(),
    var itemsToAltarId: Map<Item, Long> = emptyMap(),
    var itemsOnAltars: Map<Item, Int> = emptyMap(),

    var thmAddedThisRound: Boolean = false,
    var round: Int = 0,
    var currentStepItem: Item? = null,
    var usersToKick: List<Long> = emptyList(),
    var usersInRitual: List<Long> = emptyList(),

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