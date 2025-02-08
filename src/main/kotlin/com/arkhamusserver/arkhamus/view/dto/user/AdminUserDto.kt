package com.arkhamusserver.arkhamus.view.dto.user

import com.arkhamusserver.arkhamus.config.CultpritsUserState

data class AdminUserDto(
    val userId: Long,
    val nickName: String,
    val email: String,
    val creation: String?,
    val status: CultpritsUserState
)