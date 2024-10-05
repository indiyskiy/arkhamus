package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

enum class InGameObjectTag(
    private val visibilityModifiers: List<VisibilityModifier>
) : WithVisibilityModifiers {

    PEEKABOO_CURSE(listOf(VisibilityModifier.CULTIST));

    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        return
    }
}