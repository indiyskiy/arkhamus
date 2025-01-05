package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

enum class InGameObjectTag(
    private val visibilityModifiers: Set<VisibilityModifier>,
) : WithVisibilityModifiers {

    PEEKABOO_CURSE(setOf(VisibilityModifier.CULTIST, VisibilityModifier.PRETEND_CULTIST)),
    DARK_THOUGHTS(setOf(VisibilityModifier.CULTIST, VisibilityModifier.PRETEND_CULTIST)),
    SCENT(setOf(VisibilityModifier.CULTIST)),
    SOUND(setOf(VisibilityModifier.CULTIST));

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

}