package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.DoorState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameDoor(
    override var id: String,
    override var gameId: Long,
    var doorId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var zoneId: Long,
    var globalState: DoorState = DoorState.OPEN,
    var closedForUsers: MutableSet<Long> = mutableSetOf(),
    var visibilityModifiers: Set<VisibilityModifier>,
) : InGameEntity, WithPoint, WithTrueIngameId, WithVisibilityModifiers {

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

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}