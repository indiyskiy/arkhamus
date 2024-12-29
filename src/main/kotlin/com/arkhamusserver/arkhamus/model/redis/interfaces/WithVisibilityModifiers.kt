package com.arkhamusserver.arkhamus.model.redis.interfaces

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier

interface WithVisibilityModifiers {
    fun visibilityModifiers(): Set<VisibilityModifier>
}