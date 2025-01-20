package com.arkhamusserver.arkhamus.view.dto.steam

data class SteamUserResponse(
    val response: SteamResponseBody?
)

data class SteamResponseBody(
    val players: List<PlayerData> = emptyList()
)

data class PlayerData(
    val steamid: String,
    val communityvisibilitystate: Int,
    val profilestate: Int,
    val personaname: String,
    val profileurl: String,
    val avatar: String,
    val avatarmedium: String,
    val avatarfull: String,
    val avatarhash: String,
    val lastlogoff: Long,
    val personastate: Int,
    val primaryclanid: String,
    val timecreated: Long,
    val personastateflags: Int,
    val loccountrycode: String?
)