package com.arkhamusserver.arkhamus.model.enums.steam

enum class SteamPersonaState(val id: Int) {
    OFFLINE(0),
    ONLINE(1),
    BUSY(2),
    AWAY(3),
    SNOOZE(4),
    LOOKING_TO_TRADE(5),
    LOOKING_TO_PLAY(6);

    companion object {
        fun fromId(id: Int): SteamPersonaState {
            return values().find { it.id == id }
                ?: throw IllegalArgumentException("Unknown persona state ID: $id")
        }
    }
}
