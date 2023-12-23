package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.ItemToRecipeResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.math.min

@SpringBootTest
class CorksUsabilityUtils() {
    @Autowired
    lateinit var itemToRecipeResolver: ItemToRecipeResolver

    @Autowired
    lateinit var godToCorkResolver: GodToCorkResolver

    @Disabled
    @Test
    fun countItemsUsability() {
        val recipes: List<Pair<Item, Recipe>> = Item.values().map {
            it to itemToRecipeResolver.resolve(it)
        }
        val itemsUsability =
            recipes
                .mapNotNull {
                    it.second.ingredients
                        ?.map { it.item to it.number }
                }
                .flatten()
                .groupBy { it.first }
                .map { it.key to it.value.mapNotNull { it.second } }
                .map { it.first to it.second.sumOf { it } }
        println(Item.values().joinToString("\r\n") { item ->
            with(itemsUsability.firstOrNull { it.first == item }) {
                this?.let { "$first - $second" } ?: "${item.name} - 0"
            }
        })
    }

    @Disabled
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
        val recipe = itemToRecipeResolver.resolve(cork)
        return recipe.ingredients ?: emptyList()
    }

    private fun godsWithItems(
        firstGod: God,
        secondGod: God,
        ingredientsMap: Map<God, List<Ingredient>>
    ) = CountingCorkRelationsUtil.GodsWithItems(
        firstGod,
        secondGod,
        firstGod.getTypes().intersect(secondGod.getTypes()).size,
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
            it.key.name + " - " + it.value.joinToString(",") { it.item?.name ?: " - " }
        }.joinToString("\r\n") { it }
    }

    private fun List<CountingCorkRelationsUtil.GodsWithItems>?.convert(): String? =
        this
            ?.sortedBy { it.items }
            ?.sortedBy { it.items * it.types }
            ?.joinToString("\r\n") { it.first.name + " - " + it.second.name + ": " + it.types + ", " + it.items }

    private fun List<Ingredient>.countSimilarity(ingredients: List<Ingredient>): Int {
        var sum = 0
        this.forEach { ingredient1 ->
            ingredients.forEach { ingredient2 ->
                if (ingredient1.item == ingredient2.item) {
                    sum += min(ingredient1.number ?: 0, ingredient2.number ?: 0)
                }
            }
        }
        return sum
    }

}


