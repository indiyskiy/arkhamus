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
        val existingCell = user.items.firstOrNull { it.item == addedItem }
        if (existingCell != null) {
            existingCell.number += itemsToAdd
        } else {
            user.items += InventoryCell(
                item = addedItem,
                number = itemsToAdd
            )
        }
    }

    fun userHaveItems(user: RedisGameUser, requiredItem: Item, howManyItems: Int): Boolean {
        return howManyItems(user, requiredItem) >= howManyItems
    }

    fun userHaveItem(user: RedisGameUser, requiredItem: Item): Boolean {
        return howManyItems(user, requiredItem) > 0
    }

    fun howManyItems(user: RedisGameUser, requiredItem: Item): Int {
        return howManyItems(user.items, requiredItem)
    }

    fun howManyItems(inventory: List<InventoryCell>, requiredItem: Item): Int {
        return inventory.filter { it.item == requiredItem }.sumOf { it.number }
    }

    fun consumeItems(user: RedisGameUser, item: Item, number: Int): ConsumedItem {
        if (userHaveItem(user, item)) {
            var numberLeft = number
            user.items
                .filter { it.item == item }
                .forEach { cell ->
                    val numberToConsume = min(cell.number, numberLeft)
                    cell.number -= numberToConsume
                    numberLeft -= numberToConsume
                    logger.info("consumed $numberToConsume from user. New value in user inventory is ${cell.number}")
                }
            trimInventory(user)
        }
        return ConsumedItem(item, number)
    }

    fun consumeItem(user: RedisGameUser, item: Item) {
        consumeItems(user, item, 1)
    }

    fun mapUsersItems(items: List<InventoryCell>): List<InventoryCell> {
        return items.map {
            InventoryCell(
                item = it.item,
                number = it.number
            )
        }.sortedByDescending { it.item.id }
    }

    fun consumeItems(recipe: Recipe, gameUser: RedisGameUser, crafter: RedisCrafter): List<ConsumedItem> {
        logger.info("consuming items for recipe ${recipe.recipeId} to create ${recipe.item.name}")
        return recipe.ingredients.map { ingredient: Ingredient ->
            consumeItem(ingredient, gameUser, crafter)
        }
    }

    private fun consumeItem(ingredient: Ingredient, user: RedisGameUser, crafter: RedisCrafter): ConsumedItem {
        logger.info("consuming ${ingredient.number} of ${ingredient.item.name}")
        val itemToConsume = ingredient.item

        var toConsumeLeft = ingredient.number

        val itemsInCrafter = crafter.items.filter { it.item == itemToConsume }
        itemsInCrafter.forEach { cell ->
            val canBeConsumed = min(toConsumeLeft, cell.number)
            cell.number -= canBeConsumed
            toConsumeLeft -= canBeConsumed
            logger.info("consumed $canBeConsumed from crafter. New value in crafter is ${cell.number}")
        }
        return consumeItems(user, itemToConsume, toConsumeLeft)
    }

    private fun trimInventory(user: RedisGameUser) {
        user.items = user.items.filter { it.number > 0 && it.item != Item.PURE_NOTHING }
    }

    fun haveRequiredItems(ingredient: Ingredient, crafter: RedisCrafter, user: RedisGameUser): Boolean {
        return howManyItems(user, ingredient.item) + howManyItems(crafter, ingredient.item) >= ingredient.number
    }

    private fun howManyItems(
        crafter: RedisCrafter,
        item: Item
    ): Int {
        return crafter.items.filter { it.item == item }.sumOf { it.number }
    }

    data class ConsumedItem(var item: Item, var number: Int)

}