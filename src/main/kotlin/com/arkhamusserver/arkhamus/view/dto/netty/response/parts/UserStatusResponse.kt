package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus

data class UserStatusResponse(
    val status: InGameUserStatus,
    val started: Long
)