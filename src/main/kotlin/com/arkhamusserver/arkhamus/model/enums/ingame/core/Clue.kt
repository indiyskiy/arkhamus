package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

enum class Clue(
    val visibilityModifiers: List<VisibilityModifier>,
) : WithVisibilityModifiers {
    INSCRIPTION(listOf(VisibilityModifier.INSCRIPTION, VisibilityModifier.CULTIST)),
    SOUND(listOf(VisibilityModifier.SOUND, VisibilityModifier.CULTIST)),
    SCENT(listOf(VisibilityModifier.SCENT, VisibilityModifier.CULTIST)),
    AURA(listOf(VisibilityModifier.AURA, VisibilityModifier.CULTIST)),
    CORRUPTION(listOf(VisibilityModifier.CORRUPTION, VisibilityModifier.CULTIST)),
    OMEN(listOf(VisibilityModifier.OMEN, VisibilityModifier.CULTIST)),
    DISTORTION(listOf(VisibilityModifier.DISTORTION, VisibilityModifier.CULTIST));

    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        return
    }
}