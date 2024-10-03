package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame

data class RoleDto(
    var userRole: RoleTypeInGame? = null,
    var userClass: ClassInGame? = null,
    var userClassId: Int? = null,
)
