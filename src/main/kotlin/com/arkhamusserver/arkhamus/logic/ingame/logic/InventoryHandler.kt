package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.Long.min

@Component
class InventoryHandler {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(InventoryHandler::class.java)
    }

    fun addItem(user: RedisGameUser, addedItem: Item) {
        val howManyItems = howManyItems(user, addedItem)
        user.items[addedItem.id] = howManyItems + 1
    }

    fun userHaveItem(user: RedisGameUser, requiredItem: Item): Boolean {
        return (user.items[requiredItem.id] ?: 0) > 0
    }

    fun howManyItems(user: RedisGameUser, requiredItem: Item?): Long {
        return requiredItem?.let { user.items[it.id] } ?: 0
    }

    fun consumeItem(user: RedisGameUser, item: Item) {
        if (userHaveItem(user, item)) {
            user.items[item.id] = user.items[item.id]!! - 1
        }
    }

    fun mapUsersItems(items: Map<Int, Long>): List<InventoryCell> {
        return items.map {
            InventoryCell(
                itemId = it.key,
                number = it.value
            )
        }.sortedByDescending { it.itemId }
    }

    fun mapUsersItems(items: List<Pair<Int, Long>>): List<InventoryCell> {
        return items.map {
            InventoryCell(
                itemId = it.first,
                number = it.second
            )
        }.sortedByDescending { it.itemId }
    }

    fun consumeItems(recipe: Recipe, gameUser: RedisGameUser, crafter: RedisCrafter): List<ConsumedItem> {
        logger.info("consuming items for recipe ${recipe.recipeId} to create ${recipe.item.name}")
        return recipe.ingredients.map { ingredient: Ingredient ->
            consumeItem(ingredient, gameUser, crafter)
        }
    }

    private fun consumeItem(ingredient: Ingredient, user: RedisGameUser, crafter: RedisCrafter): ConsumedItem {
        logger.info("consuming ${ingredient.number} of ${ingredient.item.name}")
        val itemToConsume = ingredient.item.id
        val toConsumeBefore = ingredient.number.toLong()

        val itemsInCrafter = crafter.items[itemToConsume] ?: 0
        val canBeConsumedFromCrafter = min(toConsumeBefore, itemsInCrafter)
        crafter.items[itemToConsume] = itemsInCrafter - canBeConsumedFromCrafter
        logger.info("consumed $canBeConsumedFromCrafter from crafter. New value in crafter is ${crafter.items[itemToConsume]}, was $itemsInCrafter before")

        val toConsumeFromUser = toConsumeBefore - canBeConsumedFromCrafter
        val itemsFromUser = user.items[itemToConsume] ?: 0
        val canBeConsumedFromUser = min(itemsFromUser, toConsumeFromUser)
        user.items[itemToConsume] = itemsFromUser - canBeConsumedFromUser
        logger.info("consumed $canBeConsumedFromUser from user. New value in user inventory is ${user.items[itemToConsume]}, was $itemsFromUser before")
        return ConsumedItem(itemToConsume, canBeConsumedFromUser)
    }

    data class ConsumedItem(var itemId: Int, var number: Long)

}