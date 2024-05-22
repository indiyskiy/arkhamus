package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.dto.InGameUserDto
import com.arkhamusserver.arkhamus.view.dto.ingame.GodDto

data class AdminGameSessionDto(
    var id: Long? = null,
    var state: GameState? = null,
    var gameType: GameType? = null,
    var god: GodDto? = null,
    var usersInGame: List<InGameUserDto>? = emptyList(),
    var gameSessionSettings: GameSessionSettingsDto? = null,
    var token: String? = null
)