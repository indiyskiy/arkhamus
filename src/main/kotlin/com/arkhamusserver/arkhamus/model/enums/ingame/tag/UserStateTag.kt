package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility

enum class UserStateTag(
    private val visibility: Visibility?
) {
    TELEPORTATION_STUN(Visibility.PUBLIC),
    IN_RITUAL(Visibility.PUBLIC),
    INVESTIGATING(Visibility.SOURCE),
    LUMINOUS(Visibility.PUBLIC),
    FARSIGHT(Visibility.SOURCE);

    fun getVisibility(): Visibility? {
        return visibility
    }
}