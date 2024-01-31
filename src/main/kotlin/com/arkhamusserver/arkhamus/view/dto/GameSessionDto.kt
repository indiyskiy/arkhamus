package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.God


data class GameSessionDto(
    var id: Long? = null,
    var state: GameState? = null,
    var gameType: GameType? = null,
    var god: God? = null,
    var roleDtos: List<RoleDto>? = emptyList(),
    var gameSessionSettings: GameSessionSettingsDto? = null,
    var token: String? = null
)