package com.arkhamusserver.arkhamus.view.dto.user

import com.arkhamusserver.arkhamus.config.UserState

data class AdminUserDto(
    val userId: Long,
    val nickName: String,
    val email: String,
    val creation: String?,
    val state: UserState
)