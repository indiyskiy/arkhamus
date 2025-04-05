package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.math.absoluteValue
import kotlin.math.min

@SpringBootTest
class ItemsUsabilityUtils() {
    @Autowired
    lateinit var recipesSource: RecipesSource

    @Autowired
    lateinit var godToCorkResolver: GodToCorkResolver

    @Test
    fun countItemsUsability() {
        val recipes: List<Recipe> = recipesSource.getAllRecipes()
        setOf(
            ItemType.CORK,
            ItemType.INVESTIGATION,
            ItemType.USEFUL_ITEM,
            ItemType.CULTIST_ITEM,
            ItemType.ADVANCED_USEFUL_ITEM,
            ItemType.ADVANCED_CULTIST_ITEM,
        ).forEach { recipeItemType ->
            countForRecipeItemType(recipeItemType, recipes)
        }
    }

    private fun countForRecipeItemType(
        recipeItemType: ItemType,
        recipes: List<Recipe>
    ) {
        println("RECIPE TYPE: ${recipeItemType.name}")
        val filteredRecipes = recipes.filter { it.item.itemType == recipeItemType }
        val itemsUsability =
            filteredRecipes
                .asSequence()
                .flatMap{ recipe ->
                    recipe.ingredients
                        .map { it.item to it.number }
                }
                .groupBy { it.first }
                .map { it.key to it.value.map { it.second } }
                .map { it.first to it.second.sumOf { it } }

        setOf(ItemType.LOOT, ItemType.RARE_LOOT, ItemType.CULTIST_LOOT).forEach { itemType ->
            countForRecipePartType(itemType, itemsUsability)
        }
    }

    private fun countForRecipePartType(
        itemType: ItemType,
        itemsUsability: List<Pair<Item, Int>>
    ) {
        val values = Item.values()
            .sortedBy { it.itemType }
            .filter { it.itemType == itemType }
        val numbers = values.map { item ->
            itemsUsability.firstOrNull { it.first == item }?.second ?: 0
        }
        val average = numbers.average()
        println(itemType.name)
        val averageList = values.map { item ->
            val value = itemsUsability.firstOrNull { it.first == item }
            value.count(item, average)
        }.sortedBy { it.second.absoluteValue }
        averageList.forEach {
            println("${it.first.name} - ${it.second}")
        }
    }

    private fun Pair<Item, Int>?.count(item: Item, average: Double): Pair<Item, Double> =
        item to (this?.second ?.let{it-average}?: (0.0 - average))

    @Test
    fun countItemsUsabilityForCorksAndInvestigators() {
        val recipes: List<Recipe> = recipesSource.getAllRecipes().filter {
            it.item.itemType == ItemType.CORK ||
                    it.item.itemType == ItemType.INVESTIGATION
        }

        val itemsUsability =
            recipes
                .asSequence()
                .map { recipe ->
                    recipe.ingredients
                        .map { it.item to it.number }
                }
                .flatten()
                .groupBy { it.first }
                .map { it.key to it.value.map { it.second } }
                .map { it.first to it.second.sumOf { it } }
                .toList()
                .filterNot { it.second == 0 }
                .sortedByDescending { it.second }
                .sortedByDescending { it.first.itemType }
        println(itemsUsability.joinToString("\r\n") { "${it.first} - ${it.first.itemType} - ${it.second}" })
    }

    @Test
    fun listSameWithGodTypes() {
        val itemsForGod = mutableMapOf<God, List<Ingredient>>()
        God.values().forEachIndexed { _, firstGod ->
            itemsForGod[firstGod] = someItems(firstGod)
        }

        val godsWithItems = mutableListOf<CountingCorkRelationsUtil.GodsWithItems>()
        God.values().forEachIndexed { first, firstGod ->
            God.values().forEachIndexed { second, secondGod ->
                if (firstGod != secondGod && first < second) {
                    godsWithItems.add(
                        godsWithItems(firstGod, secondGod, itemsForGod)
                    )
                }
            }
        }
        val similarityStats = godsWithItems.map {
            countSimilarity(it)
        }
        val max = similarityStats.max()
        val avg = similarityStats.average()
        val worst = countWorst(similarityStats, max)
        val same = countSameMax(max, godsWithItems)

        println("Max $max")
        println("AVG $avg")
        println("worst $worst")
        println("same max $same")
        println("items ${itemsForGod.convert()}")
        println("statistic ${godsWithItems.convert()}")
    }

    private fun someItems(god: God): List<Ingredient> {
        val cork = godToCorkResolver.resolve(god)
        val recipe = recipesSource.getAllRecipes().first { it.item == cork }
        return recipe.ingredients
    }

    private fun godsWithItems(
        firstGod: God,
        secondGod: God,
        ingredientsMap: Map<God, List<Ingredient>>
    ) = CountingCorkRelationsUtil.GodsWithItems(
        firstGod,
        secondGod,
        firstGod.getTypes().intersect(secondGod.getTypes().toSet()).size,
        ingredientsMap[firstGod]!!.countSimilarity(ingredientsMap[secondGod]!!)
    )

    private fun countSimilarity(godsWithItems: CountingCorkRelationsUtil.GodsWithItems): Double =
        godsWithItems.types.toDouble() * godsWithItems.items.toDouble()


    private fun countSameMax(minMax: Double, godsWithItems: MutableList<CountingCorkRelationsUtil.GodsWithItems>): Int =
        godsWithItems.filter { countSimilarity(it).equals(minMax) }.map { listOf(it.first, it.second) }.flatten()
            .groupBy { it.name }.map { it.value.size }.maxOrNull() ?: 0

    private fun countWorst(similarityStats: List<Double>, max: Double): Int = similarityStats.filter { it == max }.size

    private fun Map<God, List<Ingredient>>.convert(): String {
        return this.map {
            it.key.name + " - " + it.value.joinToString(",") { it.item.name }
        }.joinToString("\r\n") { it }
    }

    private fun List<CountingCorkRelationsUtil.GodsWithItems>.convert(): String =
        this
            .sortedBy { it.items }
            .sortedBy { it.items * it.types }
            .joinToString("\r\n") { it.first.name + " - " + it.second.name + ": " + it.types + ", " + it.items }

    private fun List<Ingredient>.countSimilarity(ingredients: List<Ingredient>): Int {
        var sum = 0
        this.forEach { ingredient1 ->
            ingredients.forEach { ingredient2 ->
                if (ingredient1.item == ingredient2.item) {
                    sum += min(ingredient1.number, ingredient2.number)
                }
            }
        }
        return sum
    }

}


