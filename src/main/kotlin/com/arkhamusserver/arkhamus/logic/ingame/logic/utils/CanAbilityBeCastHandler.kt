package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import org.springframework.stereotype.Component

@Component
class CanAbilityBeCastHandler(
    private val abilityToClassResolver: AbilityToClassResolver,
    private val userInventoryHandler: InventoryHandler,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
    private val additionalAbilityConditions: List<AdditionalAbilityCondition>
) {

    fun canUserSeeAbility(user: RedisGameUser, ability: Ability, requiredItem: Item?): Boolean {
        return haveRequiredItem(ability, requiredItem, user) &&
                haveRelatedRole(ability, user) &&
                haveRelatedClass(ability, user)
    }

    fun abilityOfUserResponses(
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): List<AbilityOfUserResponse> {
        val visibleAbilitiesList = Ability.values().filter { canUserSeeAbility(user, it) }
        val relatedAbilityCastMap = visibleAbilitiesList.associate {
            it.id to
                    relatedAbilityCastHandler.findForUser(
                        user,
                        it,
                        globalGameData.castAbilities
                    )
        }
        val fitAdditionalConditionsMap: Map<Ability, Boolean> = visibleAbilitiesList.associateWith { ability ->
            canBeCastedAtAll(ability, user, globalGameData)
        }
        val summoningSickness = globalGameData.timeEvents.firstOrNull {
            it.type == RedisTimeEventType.SUMMONING_SICKNESS &&
                    it.state == RedisTimeEventState.ACTIVE
        }
        val availableAbilities = visibleAbilitiesList.map {
            val cooldown = if (summoningSickness != null) {
                summoningSickness.timeLeft
            } else {
                relatedAbilityCastMap[it.id]?.timeLeftCooldown ?: 0
            }
            val maxCooldown = if (summoningSickness != null) {
                summoningSickness.type.getDefaultTime()
            } else {
                (relatedAbilityCastMap[it.id]?.let { it.timeLeftCooldown + it.timePast } ?: 0)
            }
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

    fun canBeCastedAtAll(
        ability: Ability,
        user: RedisGameUser,
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
        user: RedisGameUser,
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

    private fun canUserSeeAbility(user: RedisGameUser, ability: Ability): Boolean {
        return canUserSeeAbility(user, ability, abilityToItemResolver.resolve(ability))
    }

    private fun charges(
        ability: Ability,
        user: RedisGameUser
    ) = if (ability.consumesItem) {
        abilityToItemResolver.resolve(ability)?.let { item -> numberOfRequiredItems(ability, item, user) }
    } else null

    private fun haveRelatedClass(ability: Ability, user: RedisGameUser): Boolean =
        (!ability.classBased) || (abilityToClassResolver.resolve(ability)?.contains(user.classInGame) == true)

    private fun haveRelatedRole(ability: Ability, user: RedisGameUser): Boolean =
        user.role in ability.availableForRole

    private fun haveRequiredItem(ability: Ability, requiredItem: Item?, user: RedisGameUser): Boolean =
        !ability.requiresItem || requiredItem == null || userInventoryHandler.userHaveItem(user, requiredItem)

    private fun numberOfRequiredItems(ability: Ability, requiredItem: Item?, user: RedisGameUser): Int? {
        if (!ability.requiresItem) {
            return null
        }
        if (requiredItem == null) {
            return null
        }
        return userInventoryHandler.howManyItems(user, requiredItem)
    }


}