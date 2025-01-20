package com.arkhamusserver.arkhamus.view.dto.steam

data class SteamUserResponse(
    val response: SteamResponseBody?
)

data class SteamResponseBody(
    val players: List<PlayerData> = emptyList()
)

data class PlayerData(
    val steamId: String,
    val personaName: String?,
    val avatar: String?,
    val avatarMedium: String?,
    val avatarFull: String?,
    val profileUrl: String?,
    val lastLogOff: Long?,
    val timeCreated: Long?
)