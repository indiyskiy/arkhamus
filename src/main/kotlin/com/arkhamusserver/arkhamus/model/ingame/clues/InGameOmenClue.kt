package com.arkhamusserver.arkhamus.model.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

data class InGameOmenClue(
    override val id: String,
    override val gameId: Long,
    val userId: Long,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,
    var castedAbilityUsers: Set<Long> = setOf(),
    var interactionRadius: Double = 0.0
) : InGameEntity, WithStringId, WithVisibilityModifiers {

    override fun stringId(): String {
        return id
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

}