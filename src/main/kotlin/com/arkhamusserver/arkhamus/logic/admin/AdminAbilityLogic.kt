package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.logic.globalUtils.TimeBaseCalculator
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.model.enums.ingame.core.*
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityBrowserDto
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityBrowserSimpleDto
import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import org.springframework.stereotype.Component

@Component
class AdminAbilityLogic(
    private val abilityToItemResolver: AbilityToItemResolver,
    private val abilityToClassResolver: AbilityToClassResolver,
    private val timeBaseCalculator: TimeBaseCalculator
) {
    fun listAllAbilities(): List<AbilityBrowserSimpleDto> {
        return Ability.values().toList().mapToSimpleDto()
    }

    fun getAbility(id: Int): AbilityBrowserDto? {
        val ability = id.toAbility()
        return ability?.let { abilityNotNull ->
            mapToFullDto(
                abilityNotNull,
                item = if (abilityNotNull.requiresItem) {
                    abilityToItemResolver.resolve(abilityNotNull)
                } else {
                    null
                },
                classes = if (abilityNotNull.classBased) {
                    abilityToClassResolver.resolve(abilityNotNull)
                } else {
                    null
                }
            )
        }
    }

    private fun mapToFullDto(
        ability: Ability,
        item: Item?,
        classes: Set<ClassInGame>?
    ): AbilityBrowserDto? {
        return AbilityBrowserDto(
            id = ability.id,
            name = ability.name,
            requiresItem = ability.requiresItem,
            consumesItem = ability.consumesItem,
            classBased = ability.classBased,
            availableForRole = ability.availableForRole,
            cooldown = timeBaseCalculator.resolveAbilityCooldown(ability),
            active = timeBaseCalculator.resolveAbilityActive(ability),
            globalCooldown = ability.globalCooldown,
            range = ability.range,
            visibilityModifiers = ability.visibilityModifiers,
            requireItemInfo = item?.let {
                ItemInformationDto(
                    it.id, it, it.itemType
                )
            },
            requiredClasses = classes?.toList(),
            requiresTarget = ability.targetTypes?.isNotEmpty() == true,
            targetTypes = ability.targetTypes,
        )
    }

    private fun List<Ability>.mapToSimpleDto(): List<AbilityBrowserSimpleDto> {
        return this.map {
            AbilityBrowserSimpleDto(
                id = it.id,
                name = it.name,
                requiresItem = it.requiresItem,
                roleBased = it.availableForRole.isNotEmpty() && it.availableForRole.size < RoleTypeInGame.values().size,
                classBased = it.classBased,
            )
        }
    }
}

