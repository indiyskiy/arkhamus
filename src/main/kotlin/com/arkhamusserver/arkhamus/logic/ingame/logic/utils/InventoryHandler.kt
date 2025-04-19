package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component
import kotlin.math.min

@Component
class InventoryHandler {

    companion object {
        private val logger = LoggingUtils.getLogger<InventoryHandler>()
    }

    fun itemCanBeAdded(user: InGameUser, addedItem: Item?): Boolean {
        if(addedItem == null) return false
        trimInventory(user)
        val existingCell = user.additionalData.inventory.items.firstOrNull { it.item == addedItem }
        return if (existingCell != null) {
            true
        } else {
            (user.additionalData.inventory.items.size < user.additionalData.inventory.maxItems)
        }
    }

    fun addItem(user: InGameUser, addedItem: Item) {
        addItems(user, addedItem)
    }

    fun addItems(user: InGameUser, addedItem: Item, itemsToAdd: Int = 1) {
        val existingCell = user.additionalData.inventory.items.firstOrNull { it.item == addedItem }
        if (existingCell != null) {
            existingCell.number += itemsToAdd
        } else {
            user.additionalData.inventory.items += InventoryCell(
                item = addedItem,
                number = itemsToAdd
            )
        }
    }

    fun userHaveItems(user: InGameUser, requiredItem: Item, howManyItems: Int): Boolean {
        return howManyItems(user, requiredItem) >= howManyItems
    }

    fun userHaveItem(user: InGameUser, requiredItem: Item): Boolean {
        return howManyItems(user, requiredItem) > 0
    }

    fun howManyItems(user: InGameUser, requiredItem: Item): Int {
        return howManyItems(user.additionalData.inventory.items, requiredItem)
    }

    fun howManyItems(inventory: List<InventoryCell>, requiredItem: Item): Int {
        return inventory.filter { it.item == requiredItem }.sumOf { it.number }
    }

    fun consumeItems(user: InGameUser, item: Item, number: Int): ConsumedItem {
        if (userHaveItem(user, item)) {
            var numberLeft = number
            user.additionalData.inventory.items
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

    fun consumeItem(user: InGameUser, item: Item) {
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

    fun consumeItems(recipe: Recipe, gameUser: InGameUser, crafter: InGameCrafter): List<ConsumedItem> {
        logger.info("consuming items for recipe ${recipe.recipeId} to create ${recipe.item.name}")
        return recipe.ingredients.map { ingredient: Ingredient ->
            consumeItem(ingredient, gameUser, crafter)
        }
    }

    fun haveRequiredItems(ingredient: Ingredient, crafter: InGameCrafter, user: InGameUser): Boolean {
        return howManyItems(user, ingredient.item) + howManyItems(crafter, ingredient.item) >= ingredient.number
    }

    private fun consumeItem(ingredient: Ingredient, user: InGameUser, crafter: InGameCrafter): ConsumedItem {
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

    private fun trimInventory(user: InGameUser) {
        user.additionalData.inventory.items = user.additionalData.inventory.items.filter {
            it.number > 0 && it.item != Item.PURE_NOTHING
        }
        val grouped = user.additionalData.inventory.items.groupBy { it.item }.map { (item, cells) ->
            InventoryCell(item, cells.sumOf { it.number })
        }
        user.additionalData.inventory.items = grouped.sortedByDescending { it.item.id }
    }

    private fun howManyItems(
        crafter: InGameCrafter,
        item: Item
    ): Int {
        return crafter.items.filter { it.item == item }.sumOf { it.number }
    }

    data class ConsumedItem(var item: Item, var number: Int)

}