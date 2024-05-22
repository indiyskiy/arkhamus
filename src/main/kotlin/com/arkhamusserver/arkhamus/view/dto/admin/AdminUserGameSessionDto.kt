package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame

data class AdminUserGameSessionDto(
    var roleInGame: RoleTypeInGame? = null,
    var classInGame: ClassInGame? = null,
    var gameSession: AdminGameSessionDto? = null
    )