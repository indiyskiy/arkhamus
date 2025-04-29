package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability

import com.arkhamusserver.arkhamus.logic.globalUtils.TimeBaseCalculator
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameAbilityCooldown
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import org.springframework.stereotype.Component

@Component
class CanAbilityBeCastHandler(
    private val abilityToClassResolver: AbilityToClassResolver,
    private val userInventoryHandler: InventoryHandler,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
    private val additionalAbilityConditions: List<AdditionalAbilityCondition>,
    private val timeBaseCalculator: TimeBaseCalculator
) {

    fun canUserSeeAbility(user: InGameUser, ability: Ability, requiredItem: Item?): Boolean {
        return haveRequiredItem(ability, requiredItem, user) &&
                haveRelatedRole(ability, user) &&
                haveRelatedClass(ability, user)
    }

    fun abilityOfUserResponses(
        user: InGameUser,
        globalGameData: GlobalGameData
    ): List<AbilityOfUserResponse> {
        val visibleAbilitiesList = Ability.values().filter { canUserSeeAbility(user, it) }
        val relatedAbilityCooldownMap = visibleAbilitiesList.associate {
            it.id to
                    relatedAbilityCastHandler.findCooldownsForUser(
                        user,
                        it,
                        globalGameData.abilityCooldown
                    )
        }
        val fitAdditionalConditionsMap: Map<Ability, Boolean> = visibleAbilitiesList.associateWith { ability ->
            canBeCastedAtAll(ability, user, globalGameData)
        }
        val summoningSickness = globalGameData.timeEvents.firstOrNull {
            it.type == InGameTimeEventType.SUMMONING_SICKNESS &&
                    it.state == InGameTimeEventState.ACTIVE
        }
        val availableAbilities = visibleAbilitiesList.map {
            val cooldown = summoningSickness?.timeLeft ?: (relatedAbilityCooldownMap[it.id]?.timeLeftCooldown ?: 0)
            val maxCooldown = getMaxCooldown(summoningSickness, relatedAbilityCooldownMap, it)
            val canBeCast = (fitAdditionalConditionsMap[it] != false) && (cooldown <= 0)
            val charges = charges(it, user)
            AbilityOfUserResponse(
                abilityId = it.id,
                maxCooldown = maxCooldown,
                canBeCast = canBeCast,
                cooldown = cooldown,
                charges = charges
            )
        }
        return availableAbilities
    }

    private fun getMaxCooldown(
        summoningSickness: InGameTimeEvent?,
        relatedAbilityCastMap: Map<Int, InGameAbilityCooldown?>,
        ability: Ability
    ): Long = summoningSicknessTime(summoningSickness) ?: countRealMaxCooldown(relatedAbilityCastMap, ability)

    private fun countRealMaxCooldown(
        relatedAbilityCastMap: Map<Int, InGameAbilityCooldown?>,
        ability: Ability
    ): Long = (relatedAbilityCastMap[ability.id]?.let { it.timeLeftCooldown + it.timePast } ?: 0)

    private fun summoningSicknessTime(summoningSickness: InGameTimeEvent?): Long? =
        summoningSickness?.let { timeBaseCalculator.resolve(it.type) }

    fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ) = additionalAbilityConditions.filter {
        it.accepts(ability)
    }.let { conditions ->
        conditions.isEmpty() ||
                conditions.all {
                    it.canBeCastedAtAll(ability, user, globalGameData)
                }
    }

    fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ) = additionalAbilityConditions.filter { it.accepts(ability) }.let { conditions ->
        conditions.isEmpty() || conditions.all {
            it.canBeCastedRightNow(
                ability,
                user,
                target,
                globalGameData
            )
        }
    }

    private fun canUserSeeAbility(user: InGameUser, ability: Ability): Boolean {
        return canUserSeeAbility(user, ability, abilityToItemResolver.resolve(ability))
    }

    private fun charges(
        ability: Ability,
        user: InGameUser
    ) = if (ability.consumesItem) {
        abilityToItemResolver.resolve(ability)?.let { item -> numberOfRequiredItems(ability, item, user) }
    } else null

    private fun haveRelatedClass(ability: Ability, user: InGameUser): Boolean =
        (!ability.classBased) || (abilityToClassResolver.resolve(ability)?.contains(user.classInGame) == true)

    private fun haveRelatedRole(ability: Ability, user: InGameUser): Boolean =
        user.role in ability.availableForRole

    private fun haveRequiredItem(ability: Ability, requiredItem: Item?, user: InGameUser): Boolean =
        !ability.requiresItem || requiredItem == null || userInventoryHandler.userHaveItem(user, requiredItem)

    private fun numberOfRequiredItems(ability: Ability, requiredItem: Item?, user: InGameUser): Int? {
        if (!ability.requiresItem) {
            return null
        }
        if (requiredItem == null) {
            return null
        }
        return userInventoryHandler.howManyItems(user, requiredItem)
    }


}