package com.arkhamusserver.arkhamus.view.dto.steam

class SteamUserResponse {
    data class SteamUserResponse(
        var response: PlayersResponse? = null,
    )

    data class PlayersResponse(
        var players: List<Player> = emptyList()
    )

    data class Player(
        var steamid: String? = null,
        var communityvisibilitystate: Int? = null,
        var profilestate: Int? = null,
        var personaname: String? = null,
        var profileurl: String? = null,
        var avatar: String? = null,
        var avatarmedium: String? = null,
        var avatarfull: String? = null,
        var avatarhash: String? = null,
        var lastlogoff: Long? = null,
        var personastate: Int? = null,
        var realname: String? = null,
        var primaryclanid: String? = null,
        var timecreated: Long? = null,
        var personastateflags: Int? = null,
        var loccountrycode: String? = null,
        var locstatecode: String? = null,
        var loccityid: Int? = null,
    )
}