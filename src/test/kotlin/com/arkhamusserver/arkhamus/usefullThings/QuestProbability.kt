package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts.CorkRecipePart
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts.InvestigationRecipePart
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import org.junit.jupiter.api.Test
import kotlin.random.Random

class QuestProbability {
    companion object {
        private const val numberOfQuests = 50
        private const val timesToRepeat = 10_000
        private const val SLOTS_FOR_ONE_QUEST = 4
        private val random = Random(System.currentTimeMillis())
    }

    @Test
    fun getAllInvestigationItems() {
        val distinctItems = Item.values().filter { it.itemType in setOf(ItemType.LOOT, ItemType.RARE_LOOT) }.toSet()
        val recipes = InvestigationRecipePart().recipes()
        val itemsForRecipes = recipes.map { it.ingredients.map { it.item } }.flatten().toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, SLOTS_FOR_ONE_QUEST)
                    val reward = pickReward(lootedItemsSet, possibleRewards, usefulItems)
                    lootedItemsSet.add(reward)
                }
                if (lootedItemsSet.filter { it != Item.PURE_NOTHING && usefulItems.contains(it) }.size == usefulItems.size) {
                    positiveCases++
                }
            }
            val percent = (1.0 * positiveCases) / timesToRepeat
            println("$currentNumberOfQuests - $percent")
        }
    }

    @Test
    fun getExactInvestigationItems() {
        val distinctItems = Item.values().filter { it.itemType in setOf(ItemType.LOOT, ItemType.RARE_LOOT) }.toSet()
        val recipe = InvestigationRecipePart().recipes().random(random)
        val itemsForRecipes = recipe.ingredients.map { it.item }.toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, SLOTS_FOR_ONE_QUEST)
                    val reward = pickReward(lootedItemsSet, possibleRewards, usefulItems)
                    lootedItemsSet.add(reward)
                }
                if (lootedItemsSet.filter { it != Item.PURE_NOTHING && usefulItems.contains(it) }.size == usefulItems.size) {
                    positiveCases++
                }
            }
            val percent = (1.0 * positiveCases) / timesToRepeat
            println("$currentNumberOfQuests - $percent")
        }
    }

    @Test
    fun getExactCorkItem() {
        val distinctItems = Item.values().filter { it.itemType in setOf(ItemType.LOOT, ItemType.RARE_LOOT) }.toSet()
        val recipe = CorkRecipePart().recipes().random(random)
        val itemsForRecipes = recipe.ingredients.map { it.item }.toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, SLOTS_FOR_ONE_QUEST)
                    val reward = pickReward(lootedItemsSet, possibleRewards, usefulItems)
                    lootedItemsSet.add(reward)
                }
                if (lootedItemsSet.filter { it != Item.PURE_NOTHING && usefulItems.contains(it) }.size == usefulItems.size) {
                    positiveCases++
                }
            }
            val percent = (1.0 * positiveCases) / timesToRepeat
            println("$currentNumberOfQuests - $percent")
        }
    }

    private fun pickReward(
        lootedItemsSet: MutableSet<Item>,
        possibleRewards: Set<Item>,
        usefulItems: Set<Item>
    ): Item {
        if (possibleRewards.isEmpty()) return Item.PURE_NOTHING
        if (possibleRewards.size == 1) {
            return possibleRewards.first()
        }
        return possibleRewards.firstOrNull {
            !lootedItemsSet.contains(it) && usefulItems.contains(it)
        } ?: Item.PURE_NOTHING
    }

    private fun generateRewards(
        distinctItems: Set<Item>,
        slotsFromQuest: Int,
    ): Set<Item> {
        return distinctItems.shuffled(random).take(slotsFromQuest).toSet()
    }
}


