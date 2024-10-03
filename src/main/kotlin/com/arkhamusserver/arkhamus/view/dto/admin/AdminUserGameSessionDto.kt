package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame

data class AdminUserGameDataDto(
    val wins: Int = 0,
    val losses: Int = 0,
    val winrate: Int = 0,
    val games: List<AdminUserGameSessionDto>
)

data class AdminUserGameSessionDto(
    var roleInGame: RoleTypeInGame? = null,
    var classInGame: ClassInGame? = null,
    var winOrLoose: String? = null,
    var gameSession: AdminGameSessionDto? = null
)