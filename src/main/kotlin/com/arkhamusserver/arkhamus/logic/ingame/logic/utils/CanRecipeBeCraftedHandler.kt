package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class CanRecipeBeCraftedHandler(
    private val userInventoryHandler: InventoryHandler,
) {

    fun canUserCraft(user: RedisGameUser, recipe: Recipe, crafter: RedisCrafter): Boolean {
        return haveRequiredItems(recipe, crafter, user) &&
                rightTypeOfCrafter(recipe, crafter) &&
                crafterHoldByMe(user, crafter)
    }

    private fun crafterHoldByMe(user: RedisGameUser, crafter: RedisCrafter) =
        crafter.holdingUser == user.userId

    private fun rightTypeOfCrafter(
        recipe: Recipe,
        crafter: RedisCrafter
    ): Boolean =
        crafter.crafterType in recipe.crafterTypes

    private fun haveRequiredItems(
        recipe: Recipe,
        crafter: RedisCrafter,
        user: RedisGameUser
    ): Boolean {
        return recipe.ingredients.all {
            haveRequiredItems(it, crafter, user)
        }
    }

    private fun haveRequiredItems(
        recipe: Ingredient,
        crafter: RedisCrafter,
        user: RedisGameUser
    ): Boolean {
        return itemsInCrafter(crafter, recipe) + ownItems(user, recipe) >= recipe.number
    }

    private fun ownItems(
        user: RedisGameUser,
        recipe: Ingredient
    ) = userInventoryHandler.howManyItems(user, recipe.item)

    private fun itemsInCrafter(
        crafter: RedisCrafter,
        recipe: Ingredient
    ) = crafter.items[recipe.item.id] ?: 0

}