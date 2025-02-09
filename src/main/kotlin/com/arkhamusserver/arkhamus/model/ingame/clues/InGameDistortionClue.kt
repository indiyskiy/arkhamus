package com.arkhamusserver.arkhamus.model.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameDistortionClue(
    override val id: String,
    override val gameId: Long,
    val inGameDistortionId: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val interactionRadius: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,

    val effectRadius: Double,
    val receiver: InGameDistortionClue? = null,

    var castedAbilityUsers: Set<Long> = setOf(),

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
        return inGameDistortionId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}