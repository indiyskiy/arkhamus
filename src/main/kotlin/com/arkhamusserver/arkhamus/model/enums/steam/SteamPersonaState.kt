package com.arkhamusserver.arkhamus.model.enums.steam

enum class SteamPersonaState(val id: Int) {
    PERSONA_STATE_OFFLINE(0),
    PERSONA_STATE_ONLINE(1),
    PERSONA_STATE_BUSY(2),
    PERSONA_STATE_AWAY(3),
    PERSONA_STATE_SNOOZE(4),
    PERSONA_STATE_LOOKING_TO_TRADE(5),
    PERSONA_STATE_LOOKING_TO_PLAY(6),
    PERSONA_STATE_MAX(7);

    companion object {
        fun fromId(id: Int): SteamPersonaState {
            return values().find { it.id == id }
                ?: throw IllegalArgumentException("Unknown persona state ID: $id")
        }
    }
}
