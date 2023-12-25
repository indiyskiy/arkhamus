package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleInGame

data class RoleDto (
    var userId: Long? = null,
    var userName: String? = null,
    var userRole: RoleInGame? = null
)
