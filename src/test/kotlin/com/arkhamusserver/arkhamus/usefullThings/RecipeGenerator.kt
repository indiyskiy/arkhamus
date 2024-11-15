package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import org.junit.jupiter.api.Test
import kotlin.random.Random

class RecipeGenerator {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    @Test
    fun generateUniqueUsefulItems() {
        val itemsForRecipes = Item.values().filter { it.itemType == ItemType.USEFUL_ITEM }
        val itemsForIngridientsLoot = Item.values().filter { it.itemType == ItemType.LOOT }
        val itemsForIngridientsRare = Item.values().filter { it.itemType == ItemType.RARE_LOOT }
        var difference = Int.MAX_VALUE
        var finalResult = mutableSetOf<Recipe>()
        var distinctItems = 0
        repeat(1000) {
            val recipes = mutableSetOf<Recipe>()
            itemsForRecipes.forEach {
                val recipe = generateRecipeUseful(
                    itemToCraft = it,
                    possibleItemsLoot = itemsForIngridientsLoot,
                    possibleItemsRare = itemsForIngridientsRare,
                    existingRecipes = recipes
                )
                recipes.add(recipe)
            }
            val map: List<Pair<Item, Int>> = recipes.map { it.ingredients }.flatten().groupBy { it.item }
                .map { it.key to it.value.sumOf { it.number } }
            val itemsFromMap = map.map { it.first }.toSet()
            val itemsNotInMap = (
                    itemsForIngridientsLoot + itemsForIngridientsRare
                    ).filter {
                    it !in itemsFromMap
                }
            val enrichedMap = map + itemsNotInMap.map { it to 0 }
            val min = enrichedMap.minOf { it.second }
            val max = enrichedMap.maxOf { it.second }

            val distinctNew = enrichedMap.map { it.first }.distinct().size

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

    private fun generateRecipeT2(
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
        } ?: generateRecipeT2(itemToCraft, possibleItems, existingRecipes)
    }

    private fun generateRecipeUseful(
        itemToCraft: Item,
        possibleItemsLoot: List<Item>,
        possibleItemsRare: List<Item>,
        existingRecipes: MutableSet<Recipe>
    ): Recipe {
        return Recipe(
            recipeId = random.nextInt(),
            item = itemToCraft,
            ingredients = listOf(
                Ingredient(possibleItemsLoot.random(random), 3),
                Ingredient(possibleItemsRare.random(random), 1),
            )
        ).takeIf {
            newRecipe(it, existingRecipes)
        } ?: generateRecipeUseful(itemToCraft, possibleItemsLoot, possibleItemsRare, existingRecipes)
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
}