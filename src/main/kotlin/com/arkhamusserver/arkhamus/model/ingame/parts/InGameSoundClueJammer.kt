package com.arkhamusserver.arkhamus.model.ingame.parts

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameSoundClueJammer(
    override val id: String,
    override val gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var inGameId: Long,
    var visibilityModifiers: Set<VisibilityModifier>,
    var interactionRadius: Double,
    var soundClueId: Long,
    var zoneId: Long,
    var turnedOn : Boolean
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
        return inGameId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}