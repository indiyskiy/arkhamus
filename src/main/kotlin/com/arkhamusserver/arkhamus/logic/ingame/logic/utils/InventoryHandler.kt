package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class InventoryHandler {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(InventoryHandler::class.java)
    }

    fun addItem(user: RedisGameUser, addedItem: Item) {
        addItems(user, addedItem)
    }

    fun addItems(user: RedisGameUser, addedItem: Item, itemsToAdd: Int = 1) {
        addItems(user, addedItem.id, itemsToAdd)
    }

    fun addItems(user: RedisGameUser, addedItemId: Int, itemsToAdd: Int = 1) {
        val howManyItems = howManyItems(user, addedItemId)
        user.items[addedItemId] = howManyItems + itemsToAdd
    }

    fun userHaveItems(user: RedisGameUser, requiredItemId: Int?, howManyItems: Int): Boolean {
        return howManyItems(user, requiredItemId) >= howManyItems
    }

    fun userHaveItem(user: RedisGameUser, requiredItem: Item): Boolean {
        return howManyItems(user, requiredItem) > 0
    }

    fun userHaveItem(user: RedisGameUser, requiredItem: Int): Boolean {
        return howManyItems(user, requiredItem) > 0
    }

    fun howManyItems(user: RedisGameUser, requiredItem: Item?): Int {
        return howManyItems(user, requiredItem?.id)
    }

    fun howManyItems(user: RedisGameUser, requiredItemId: Int?): Int {
        return requiredItemId?.let { user.items[it] } ?: 0
    }

    fun consumeItems(user: RedisGameUser, item: Item, number: Int) {
        consumeItems(user, item.id, number)
    }

    fun consumeItems(user: RedisGameUser, item: Int?, number: Int?) {
        if (item != null && number != null && userHaveItem(user, item)) {
            user.items[item] = user.items[item]!! - number
        }
    }

    fun consumeItem(user: RedisGameUser, item: Item) {
        consumeItems(user, item, 1)
    }

    fun mapUsersItems(items: Map<Int, Int>): List<InventoryCell> {
        return items.map {
            InventoryCell(
                itemId = it.key,
                number = it.value
            )
        }.sortedByDescending { it.itemId }
    }

    fun mapUsersItems(items: List<Pair<Int, Int>>): List<InventoryCell> {
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
        val toConsumeBefore = ingredient.number

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

    data class ConsumedItem(var itemId: Int, var number: Int)

}