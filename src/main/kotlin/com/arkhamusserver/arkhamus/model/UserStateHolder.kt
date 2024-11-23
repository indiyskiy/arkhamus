package com.arkhamusserver.arkhamus.model

import com.arkhamusserver.arkhamus.config.UserState

data class UserStateHolder(
    val userId: Long,
    val userState: UserState,
    val lastActive: Long,
)