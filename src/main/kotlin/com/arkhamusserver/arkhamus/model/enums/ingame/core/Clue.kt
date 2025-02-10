package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

enum class Clue(
    val visibilityModifiers: Set<VisibilityModifier>,
) : WithVisibilityModifiers {
    INSCRIPTION(
        setOf(
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
            VisibilityModifier.CULTIST
        )
    ),
    CORRUPTION(
        setOf(
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
            VisibilityModifier.CULTIST
        )
    );

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}