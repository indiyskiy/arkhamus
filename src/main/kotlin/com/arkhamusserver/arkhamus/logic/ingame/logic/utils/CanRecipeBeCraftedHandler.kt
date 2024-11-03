package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CanRecipeBeCraftedHandler(
    private val userInventoryHandler: InventoryHandler,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(CanRecipeBeCraftedHandler::class.java)
    }

    fun canUserCraft(user: RedisGameUser, recipe: Recipe, crafter: RedisCrafter, holdCrafterNeeded: Boolean = true): Boolean {
        return haveRequiredItems(recipe, crafter, user) &&
                rightTypeOfCrafter(recipe, crafter) &&
                (!holdCrafterNeeded || crafterHoldByMe(user, crafter))
    }

    private fun crafterHoldByMe(user: RedisGameUser, crafter: RedisCrafter) =
        (crafter.holdingUser == user.userId).also {
            logger.warn("crafterHoldByMe $this")
        }

    private fun rightTypeOfCrafter(
        recipe: Recipe,
        crafter: RedisCrafter
    ): Boolean =
        (crafter.crafterType in recipe.crafterTypes).also {
            logger.warn("rightTypeOfCrafter $this")
        }

    private fun haveRequiredItems(
        recipe: Recipe,
        crafter: RedisCrafter,
        user: RedisGameUser
    ): Boolean {
        return recipe.ingredients.all {
            haveRequiredItems(it, crafter, user)
        }.also {
            logger.warn("haveRequiredItems $this")
        }
    }

    private fun haveRequiredItems(
        recipe: Ingredient,
        crafter: RedisCrafter,
        user: RedisGameUser
    ): Boolean {
        return (itemsInCrafter(crafter, recipe)
                + ownItems(user, recipe) >= recipe.number
                )
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