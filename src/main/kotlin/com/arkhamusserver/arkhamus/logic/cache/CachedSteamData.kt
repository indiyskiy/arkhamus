package com.arkhamusserver.arkhamus.logic.cache

import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState

data class CachedSteamData(
    val steamId: String,
    val steamPersonaState: SteamPersonaState,
    val name: String
)
