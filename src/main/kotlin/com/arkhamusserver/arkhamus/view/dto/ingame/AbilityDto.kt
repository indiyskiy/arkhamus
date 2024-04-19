package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame

data class AbilityDto(
    val id: Int,
    val value: String,
    val title: String,
    val requireItem: Boolean,
    val requireItemId: Int?,
    val consumesItem: Boolean,
    val classBased: Boolean,
    val requiredClassId: Int?,
    val availableFor: List<RoleTypeInGame>,
    val cooldown: Long?,
    val globalCooldown: Boolean,
)