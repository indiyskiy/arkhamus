package com.arkhamusserver.arkhamus.model.enums.ingame

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