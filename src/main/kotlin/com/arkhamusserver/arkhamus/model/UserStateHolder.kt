package com.arkhamusserver.arkhamus.model

import com.arkhamusserver.arkhamus.config.CultpritsUserState

data class UserStateHolder(
    val userId: Long,
    val userState: CultpritsUserState,
    val lastActive: Long,
)