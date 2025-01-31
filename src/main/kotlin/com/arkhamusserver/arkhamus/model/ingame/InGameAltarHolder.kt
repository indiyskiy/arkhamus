package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId

data class InGameAltarHolder(
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
    var usersToKick: Set<Long> = emptySet(),
    var usersInRitual: Set<Long> = emptySet(),

    var state: MapAltarState = MapAltarState.OPEN,
) : InGameEntity, WithPoint, WithTrueIngameId {

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