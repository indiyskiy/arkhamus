package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatusType

data class InGameUserStatusDto(
    val name: String,
    val id: Int,
    val type: InGameUserStatusType
)