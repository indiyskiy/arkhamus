package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility

enum class UserStateTag(
    private val visibility: Visibility?
) {
    STUN(Visibility.PUBLIC),
    IN_RITUAL(Visibility.PUBLIC),
    INVESTIGATING(Visibility.SOURCE),
    INVESTIGATING_SCENT(Visibility.SOURCE),
    INVESTIGATING_SOUND(Visibility.SOURCE),
    LUMINOUS(Visibility.PUBLIC),
    INVULNERABILITY(Visibility.PUBLIC),
    FARSIGHT(Visibility.SOURCE);

    fun getVisibility(): Visibility? {
        return visibility
    }
}