package com.arkhamusserver.arkhamus.model.ingame.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.Interactable
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class AuraCluePoint(
    val id: String,
    val x: Double,
    var y: Double,
    val z: Double,
    val interactionRadius: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    val startDistance: Double,
) : WithPoint, WithStringId, WithVisibilityModifiers, Interactable {
    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }

    override fun stringId(): String {
        return id
    }

}