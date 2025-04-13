package com.arkhamusserver.arkhamus.model.enums.ingame.tag

import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility

enum class UserStateTag(
    private val visibility: Visibility?
) {
    STUN(Visibility.PUBLIC),
    IN_RITUAL(Visibility.PUBLIC),

    //abilities
    INVESTIGATING_SCENT(Visibility.SOURCE),
    INVESTIGATING_SOUND(Visibility.SOURCE),
    INVESTIGATING_OMEN(Visibility.SOURCE),
    INVESTIGATING_CORRUPTION(Visibility.SOURCE),
    INVESTIGATING_DISTORTION(Visibility.SOURCE),
    INVESTIGATING_AURA(Visibility.SOURCE),
    INVESTIGATING_INSCRIPTION(Visibility.SOURCE),

    //just abilities
    LUMINOUS(Visibility.PUBLIC),
    INVULNERABILITY(Visibility.PUBLIC),
    STEALTH(Visibility.SOURCE),
    FARSIGHT(Visibility.SOURCE),

    //investigator abilities
    HAVE_INCLUSION(Visibility.NONE),

    //cultist ability
    MADNESS_LINK_TARGET(Visibility.NONE),
    MADNESS_LINK_SOURCE(Visibility.NONE),
    ;

    fun getVisibility(): Visibility? {
        return visibility
    }
}