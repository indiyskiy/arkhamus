package com.arkhamusserver.arkhamus.config

enum class CultpritsUserState(
    val priority: Int,
    val forceUpdate: Boolean = false,
) {
    OFFLINE(-1, true),
    AFK(1, true),
    ONLINE(2),
    IN_LOBBY(10, true),
    IN_GAME(20, true)
}