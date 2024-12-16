package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame

data class ClassInGameDto(
    val id: Int,
    val name: String,
    val roleType: RoleTypeInGame,
    val defaultAbility: AbilityDto?,
    val turnedOn: Boolean
)