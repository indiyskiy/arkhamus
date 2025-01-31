package com.arkhamusserver.arkhamus.model.ingame.interfaces

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier

interface WithVisibilityModifiers {
    fun visibilityModifiers(): Set<VisibilityModifier>
}