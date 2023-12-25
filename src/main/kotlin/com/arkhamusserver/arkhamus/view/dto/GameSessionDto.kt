package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God


data class GameSessionDto(
    var id: Long? = null,
    var state: GameState? = null,
    var lobbySize: Int? = null,
    var numberOfCultists: Int? = null,
    var god: God? = null,
    var roleDtos: List<RoleDto>? = emptyList()
)