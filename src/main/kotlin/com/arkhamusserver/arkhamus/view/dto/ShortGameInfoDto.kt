package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.ingame.GameType

data class ShortGameInfoDto(
    val gameId: Long,
    var gameType: GameType,
    var token: String? = null,
    var lobbySize: Int? = null,
)