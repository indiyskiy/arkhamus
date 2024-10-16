package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier

data class AbilityBrowserDto(
    var id: Int,
    var name: String,
    var requiresItem: Boolean,
    var consumesItem: Boolean,
    var classBased: Boolean,
    var availableForRole: Set<RoleTypeInGame>,
    var cooldown: Long,
    var active: Long?,
    var globalCooldown: Boolean,
    var range: Double?,
    var visibilityModifiers: Set<VisibilityModifier>,
    var requireItemInfo: ItemInformationDto?,
    var requiredClasses: List<ClassInGame>?,
    var requiresTarget: Boolean,
    var targetTypes: List<GameObjectType>?,
)