package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.RecipesSource
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import org.junit.jupiter.api.Test
import kotlin.random.Random

class QuestProbability {
    companion object {
        private const val numberOfQuests = 50
        private const val timesToRepeat = 10_000
        private const val slotsFromQuest = 4
        private val random = Random(System.currentTimeMillis())
    }

    @Test
    fun getAllInvestigationItems() {
        val distinctItems = Item.values().filter { it.itemType in setOf(ItemType.LOOT, ItemType.RARE_LOOT) }.toSet()
        val investigationItems = Item.values().filter { it.itemType in setOf(ItemType.INVESTIGATION) }
        val recipes = RecipesSource().getAllRecipes().filter { it.item in investigationItems }
        val itemsForRecipes = recipes.map { it.ingredients.map { it.item } }.flatten().toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, slotsFromQuest)
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
        val investigationItems = Item.values().filter { it.itemType in setOf(ItemType.INVESTIGATION) }.random(random)
        val recipes = RecipesSource().getAllRecipes().filter { it.item == investigationItems }
        val itemsForRecipes = recipes.map { it.ingredients.map { it.item } }.flatten().toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, slotsFromQuest)
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
    fun getExactCorkItems() {
        val distinctItems = Item.values().filter { it.itemType in setOf(ItemType.LOOT, ItemType.RARE_LOOT) }.toSet()
        val cork = Item.values().filter { it.itemType in setOf(ItemType.CORK) }.random(random)
        val recipe = RecipesSource().getAllRecipes().filter { it.item == cork }
        val itemsForRecipes = recipe.map {
            it.ingredients.map { it.item }
        }.flatten()
            .map { item ->
                if (item.itemType == ItemType.RARE_LOOT) {
                    listOf(item)
                } else {
                    RecipesSource().getAllRecipes().first { it.item == item }.ingredients.map { it.item }
                }
            }.flatten().toSet()
        val usefulItems = itemsForRecipes

        repeat(numberOfQuests) { currentNumberOfQuests ->
            var positiveCases = 0
            repeat(timesToRepeat) {
                val lootedItemsSet: MutableSet<Item> = mutableSetOf()
                repeat(currentNumberOfQuests) {
                    val possibleRewards = generateRewards(distinctItems, slotsFromQuest)
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
    fun generateUniqueT2Items() {
        val itemsForRecipes = Item.values().filter { it.itemType == ItemType.CRAFT_T2 }
        val itemsForIngridients = Item.values().filter { it.itemType == ItemType.LOOT }
        var difference = Int.MAX_VALUE
        var finalResult = mutableSetOf<Recipe>()
        var distinctItems = 0
        repeat(1000) {
            val recipes = mutableSetOf<Recipe>()
            itemsForRecipes.forEach {
                val recipe = generateRecipe(
                    itemToCraft = it,
                    possibleItems = itemsForIngridients,
                    existingRecipes = recipes
                )
                recipes.add(recipe)
            }
            val map = recipes.map { it.ingredients }.flatten().groupBy { it.item }
                .map { it.key to it.value.sumOf { it.number } }
            val min = map.minOf { it.second }
            val max = map.maxOf { it.second }

            val distinctNew = map.map { it.first }.distinct().size

            if (max - min < difference) {
                difference = max - min
                finalResult = recipes
                distinctItems = distinctNew
            } else {
                if (max - min == difference && distinctItems < distinctNew) {
                    difference = max - min
                    finalResult = recipes
                    distinctItems = distinctNew
                }
            }
        }
        finalResult.forEach {
            println("${it.item} ingrs are ${it.ingredients.joinToString { "${it.item};${it.number}" }}")
        }
    }

    private fun generateRecipe(
        itemToCraft: Item,
        possibleItems: List<Item>,
        existingRecipes: MutableSet<Recipe>
    ): Recipe {
        return Recipe(
            recipeId = random.nextInt(),
            item = itemToCraft,
            ingredients = possibleItems.shuffled(random).take(2).mapIndexed { i, it -> Ingredient(it, i + 2) }
        ).takeIf {
            newRecipe(it, existingRecipes)
        } ?: generateRecipe(itemToCraft, possibleItems, existingRecipes)
    }

    private fun newRecipe(
        recipe: Recipe,
        recipes: MutableSet<Recipe>
    ): Boolean {
        return !recipes.any {
            it.ingredients.all {
                recipe.ingredients.any { ingredient ->
                    it.item == ingredient.item
                }
            }
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


