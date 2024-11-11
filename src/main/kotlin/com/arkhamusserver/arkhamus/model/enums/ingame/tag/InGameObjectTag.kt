package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

enum class InGameObjectTag(
    private val visibilityModifiers: Set<VisibilityModifier>,
    private val visibilityModifiersStrings: MutableSet<String> = visibilityModifiers.map { it.name }.toMutableSet(),
) : WithVisibilityModifiers {

    PEEKABOO_CURSE(setOf(VisibilityModifier.CULTIST, VisibilityModifier.PRETEND_CULTIST)),
    DARK_THOUGHTS(setOf(VisibilityModifier.CULTIST, VisibilityModifier.PRETEND_CULTIST)), ;

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiersStrings
    }

}