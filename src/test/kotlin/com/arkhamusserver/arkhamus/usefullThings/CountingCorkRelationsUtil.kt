package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
class CountingCorkRelationsUtil {

    @Disabled
    @Test
    fun contextLoads() {
        val random = Random(System.currentTimeMillis())
        val howManyItems = 2

        var minimum: Map<God, List<Item>>? = null
        var relatedStatistic: List<GodsWithItems>? = null
        var minMax = Double.MAX_VALUE
        var minWorst = 0
        var minSameMax = Int.MAX_VALUE
        var minAvg = Double.MAX_VALUE

        repeat(5000000) {
            val itemsForGod = mutableMapOf<God, List<Item>>()
            God.values().forEachIndexed { _, firstGod ->
                itemsForGod[firstGod] = someItems(howManyItems, itemsForGod.values.toList(), random)
            }

            val godsWithItems = mutableListOf<GodsWithItems>()
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

            if (max < minMax) {
                minimum = itemsForGod
                minMax = max
                minAvg = avg
                relatedStatistic = godsWithItems
                minWorst = worst
                minSameMax = same
            } else {
                if (max == minMax && worst < minWorst) {
                    minimum = itemsForGod
                    minMax = max
                    minAvg = avg
                    relatedStatistic = godsWithItems
                    minWorst = worst
                    minSameMax = same
                } else {
                    if (max == minMax && worst == minWorst && same < minSameMax) {
                        minimum = itemsForGod
                        minMax = max
                        minAvg = avg
                        relatedStatistic = godsWithItems
                        minWorst = worst
                        minSameMax = same
                    } else {
                        if (max == minMax && same == minSameMax && worst == minWorst && avg < minAvg) {
                            minimum = itemsForGod
                            minMax = max
                            minAvg = avg
                            relatedStatistic = godsWithItems
                            minWorst = worst
                            minSameMax = same
                        }
                    }
                }
            }
        }
        System.out.println("Max ${minMax}")
        System.out.println("AVG ${minAvg}")
        System.out.println("items ${minimum?.convert()}")
        System.out.println("statistic ${relatedStatistic?.convert()}")
    }


    private fun countSameMax(minMax: Double, godsWithItems: MutableList<GodsWithItems>): Int =
        godsWithItems.filter { countSimilarity(it).equals(minMax) }.map { listOf(it.first, it.second) }.flatten()
            .groupBy { it.name }.map { it.value.size }.maxOrNull() ?: 0

    private fun countWorst(similarityStats: List<Double>, max: Double): Int = similarityStats.filter { it == max }.size


    private fun countSimilarity(godsWithItems: GodsWithItems): Double =
        godsWithItems.types.toDouble() * godsWithItems.items.toDouble()


    private fun someItems(howManyItems: Int, existingSets: List<List<Item>>, random: Random): List<Item> {
        val items = Item.values().filter { it.getItemType() == ItemType.RARE_LOOT }.toTypedArray()
        items.shuffle(random)
        val toTake = items.take(howManyItems).sortedBy { it.name }
        if (existingSets.contains(toTake)) {
            return someItems(howManyItems, existingSets, random)
        } else {
            return toTake
        }
    }

    private fun godsWithItems(
        firstGod: God, secondGod: God, itemsMap: Map<God, List<Item>>
    ) = GodsWithItems(
        firstGod,
        secondGod,
        firstGod.getTypes().intersect(secondGod.getTypes()).size,
        itemsMap[firstGod]!!.intersect(itemsMap[secondGod]!!).size
    )


    data class GodsWithItems(
        val first: God, val second: God, val types: Int, val items: Int
    )

    private fun List<GodsWithItems>?.convert(): String? =
        this?.sortedBy { it.items }?.sortedBy { it.types }
            ?.joinToString("\r\n") { it.first.name + " - " + it.second.name + ": " + it.types + ", " + it.items }


    private fun Map<God, List<Item>>.convert(): String {
        return this.map {
            it.key.name + " - " + it.value.joinToString(",") { it.name }
        }.joinToString("\r\n") { it }
    }
}