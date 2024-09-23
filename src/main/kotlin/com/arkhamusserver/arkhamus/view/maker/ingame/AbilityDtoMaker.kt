package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityDto
import org.springframework.stereotype.Component

@Component
class AbilityDtoMaker(
    private val abilityToItemResolver: AbilityToItemResolver,
    private val abilityToClassResolver: AbilityToClassResolver
) {
    fun convert(values: Array<Ability>): List<AbilityDto> {
        return values.map { convert(it) }
    }

    fun convert(value: Ability): AbilityDto {
        return AbilityDto(
            id = value.id,
            value = value.name,
            requireItem = value.requiresItem,
            requireItemId = if (value.requiresItem) {
                abilityToItemResolver.resolve(value)?.id
            } else null,
            consumesItem = value.consumesItem,
            classBased = value.classBased,
            requiredClassIds = if (value.classBased) {
                abilityToClassResolver.resolve(value)?.map { it.id }
            } else null,
            availableFor = value.availableForRole.toList(),
            cooldown = value.cooldown,
            globalCooldown = value.globalCooldown,
            targetTypes = value.targetTypes,
            requiresTarget = !value.targetTypes.isNullOrEmpty(),
            range = value.range,
        )
    }
}