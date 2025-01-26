package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

enum class Clue(
    val visibilityModifiers: Set<VisibilityModifier>,
) : WithVisibilityModifiers {
    INSCRIPTION(
        setOf(
            VisibilityModifier.INSCRIPTION,
            VisibilityModifier.CULTIST
        )
    ),
    SOUND(
        setOf(
            VisibilityModifier.CULTIST
        )
    ),
    SCENT(
        setOf(
            VisibilityModifier.CULTIST
        )
    ),
    AURA(
        setOf(
            VisibilityModifier.AURA,
            VisibilityModifier.CULTIST
        )
    ),
    CORRUPTION(
        setOf(
            VisibilityModifier.CORRUPTION,
            VisibilityModifier.CULTIST
        )
    ),
    OMEN(
        setOf(
            VisibilityModifier.CULTIST
        )
    ),
    DISTORTION(
        setOf(
            VisibilityModifier.DISTORTION,
            VisibilityModifier.CULTIST
        )
    );

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}