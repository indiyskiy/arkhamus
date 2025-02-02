package com.arkhamusserver.arkhamus.model.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameCorruptionClue(
    override val id: String,
    override val gameId: Long,
    val inGameCorruptionId: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val interactionRadius: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,

    var castedAbilityUsers: Set<Long> = setOf(),
    var timeUntilFullyGrowth: Long,
    var totalTimeUntilNullify: Long,
    var timeFromStart: Long = 0L,
) : InGameEntity, WithPoint, WithTrueIngameId, WithVisibilityModifiers, Interactable {

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
        return inGameCorruptionId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}