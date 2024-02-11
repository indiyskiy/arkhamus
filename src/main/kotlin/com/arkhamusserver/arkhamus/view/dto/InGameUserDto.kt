package com.arkhamusserver.arkhamus.view.dto

data class InGameUserDto(
    var role: RoleDto? = null,
    var userId: Long? = null,
    var userName: String? = null,
    var isHost: Boolean = false
)
