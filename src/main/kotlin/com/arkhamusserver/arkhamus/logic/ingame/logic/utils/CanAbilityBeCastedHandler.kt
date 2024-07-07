package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import org.springframework.stereotype.Component

@Component
class CanAbilityBeCastedHandler(
    private val abilityToClassResolver: AbilityToClassResolver,
    private val userInventoryHandler: InventoryHandler,
    private val abilityToItemResolver: AbilityToItemResolver,
    private val relatedAbilityCastHandler: RelatedAbilityCastHandler,
) {
    fun canUserCast(user: RedisGameUser, ability: Ability): Boolean {
        return canUserCast(user, ability, abilityToItemResolver.resolve(ability))
    }

    fun canUserCast(user: RedisGameUser, ability: Ability, requiredItem: Item?): Boolean {
        return haveRequiredItem(ability, requiredItem, user) &&
                haveRelatedRoleType(ability, user) &&
                haveRelatedRole(ability, user)
    }

    fun abilityOfUserResponses(
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ): List<AbilityOfUserResponse> {
        val canBeCastedAbilitiesList = Ability.values().filter { canUserCast(user, it) }
        val relatedAbilityCastMap = canBeCastedAbilitiesList.associate {
            it.id to
                    relatedAbilityCastHandler.findForUser(
                        user,
                        it,
                        globalGameData.castedAbilities
                    )
        }
        val availableAbilities = canBeCastedAbilitiesList.map {
            AbilityOfUserResponse(
                abilityId = it.id,
                maxCooldown = it.cooldown,
                cooldown = relatedAbilityCastMap[it.id]?.timeLeftCooldown ?: 0,
                charges = numberOfRequiredItems(it, abilityToItemResolver.resolve(it), user)
            )
        }
        return availableAbilities
    }

    private fun haveRelatedRole(ability: Ability, user: RedisGameUser): Boolean =
        (!ability.classBased) || (abilityToClassResolver.resolve(ability)?.contains(user.classInGame) ?: false)

    private fun haveRelatedRoleType(ability: Ability, user: RedisGameUser): Boolean =
        user.role in ability.availableFor

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