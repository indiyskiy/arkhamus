package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame

data class AbilityDto(
    val id: Int,
    val value: String,
    val requireItem: Boolean,
    val requireItemId: Int?,
    val consumesItem: Boolean,
    val classBased: Boolean,
    val requiredClassIds: List<Int>?,
    val availableFor: List<RoleTypeInGame>,
    val cooldown: Long,
    val globalCooldown: Boolean,
    val targetTypes: List<GameObjectType>?,
    val requiresTarget: Boolean,
    val range: Double?
)