package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame

data class RoleDto(
    var userRole: RoleTypeInGame? = null,
    var userClass: ClassInGame? = null,
)
