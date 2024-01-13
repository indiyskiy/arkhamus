package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame

data class RoleDto (
    var userId: Long? = null,
    var userName: String? = null,
    var userRole: RoleTypeInGame? = null
)
