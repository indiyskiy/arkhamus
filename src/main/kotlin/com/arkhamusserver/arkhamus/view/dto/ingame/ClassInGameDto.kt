package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame

data class ClassInGameDto(
    val id: Int,
    val name: String,
    val value: String,
    val roleType: RoleTypeInGame
)