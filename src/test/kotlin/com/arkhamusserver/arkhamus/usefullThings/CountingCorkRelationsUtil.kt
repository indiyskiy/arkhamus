package com.arkhamusserver.arkhamus.usefullThings

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType
import org.assertj.core.util.DateUtil
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random


@SpringBootTest
class CountingCorkRelationsUtil {

    companion object {
        const val HOW_MANY_ITEMS = 3
        val random = Random(System.currentTimeMillis())
    }

    @Disabled
    @Test
    fun contextLoads() {
        var minimum: Map<God, Set<Item>>? = null
        var relatedStatistic: List<GodsWithItems>? = null
        var minMax = Double.MAX_VALUE
        var minWorst = 0
        var minSameMax = Int.MAX_VALUE
        var minAvg = Double.MAX_VALUE
        var minUsabilityDiff = Int.MAX_VALUE
        val possibleItems =
            Item.values().filter { it.getItemType() == ItemType.RARE_LOOT }.toList()
        val maxValue = howManySameItemsPossible(possibleItems)
        //start counting
        repeat(50_000_000) { iterationNumber ->
            if (iterationNumber % 500_000 == 0) {
                println(iterationNumber.toString() + " " + DateUtil.now())
            }
            val itemsForGod = fillItemsForGod(possibleItems, maxValue)

            val godsWithItems = countGodsWithItems(itemsForGod)
            val similarityStats = countSimilarityStats(godsWithItems)

            val max = similarityStats.max()
            val avg = similarityStats.average()
            val worst = countWorst(similarityStats, max)
            val same = countSameMax(max, godsWithItems)
            val usabilityDiff = countUsabilityDiff(itemsForGod)

            var changed = false

            if (max < minMax) {
                println("changed by max $max")
                minimum = itemsForGod
                minMax = max
                minAvg = avg
                relatedStatistic = godsWithItems
                minWorst = worst
                minSameMax = same
                minUsabilityDiff = usabilityDiff
                changed = true
            } else {
                if (max == minMax && worst < minWorst) {
                    println("changed by worst $worst")
                    minimum = itemsForGod
                    minMax = max
                    minAvg = avg
                    relatedStatistic = godsWithItems
                    minWorst = worst
                    minSameMax = same
                    minUsabilityDiff = usabilityDiff
                    changed = true
                } else {
                    if (max == minMax && worst == minWorst && same < minSameMax) {
                        println("changed by same $same")
                        minimum = itemsForGod
                        minMax = max
                        minAvg = avg
                        relatedStatistic = godsWithItems
                        minWorst = worst
                        minSameMax = same
                        minUsabilityDiff = usabilityDiff
                        changed = true
                    } else {
                        if (max == minMax && same == minSameMax && worst == minWorst && minUsabilityDiff > usabilityDiff) {
                            println("changed by usability $minUsabilityDiff -> $usabilityDiff")
                            minimum = itemsForGod
                            minMax = max
                            minAvg = avg
                            relatedStatistic = godsWithItems
                            minWorst = worst
                            minSameMax = same
                            minUsabilityDiff = usabilityDiff
                            changed = true
                        } else {
                            if (max == minMax && same == minSameMax && worst == minWorst && minUsabilityDiff == usabilityDiff && avg < minAvg) {
                                println("changed by avg $avg")
                                minimum = itemsForGod
                                minMax = max
                                minAvg = avg
                                relatedStatistic = godsWithItems
                                minWorst = worst
                                minSameMax = same
                                minUsabilityDiff = usabilityDiff
                                changed = true
                            }
                        }
                    }
                }
            }
            if (changed) {
                printStatistic(minMax, minWorst, minAvg, minimum, relatedStatistic)
            }
        }
        printStatistic(minMax, minWorst, minAvg, minimum, relatedStatistic, true)
    }

    private fun printStatistic(
        minMax: Double,
        minWorst: Int,
        minAvg: Double,
        minimum: Map<God, Set<Item>>?,
        relatedStatistic: List<GodsWithItems>?,
        final: Boolean = false
    ) {
        if (final) {
            println("___________________________________")
        } else {
            println("_______________FINAL_______________")
        }
        println("Max $minMax")
        println("Worst $minWorst")
        println("AVG $minAvg")
        println("items ${minimum?.convert()}")
        println("statistic ${relatedStatistic?.convert()}")
    }


    private fun howManySameItemsPossible(
        possibleItems: List<Item>
    ) = if ((God.values().size * HOW_MANY_ITEMS) % possibleItems.size == 0) {
        (God.values().size * HOW_MANY_ITEMS) / possibleItems.size
    } else {
        (God.values().size * HOW_MANY_ITEMS) / possibleItems.size + 1
    }

    private fun fillItemsForGod(
        possibleItems: List<Item>,
        maxValue: Int,
    ): Map<God, MutableSet<Item>> {
        val itemsForGod = God.values().toList().shuffled(random).associateWith { mutableSetOf<Item>() }
        firstGodFixed(itemsForGod)
        var failCounter = 0
        while (!isEnoughItemsForGods(itemsForGod)) {
            val itemsToAdd = searchItemsThatIsNotEnough(possibleItems, itemsForGod, maxValue)
            val godsToAdd = godsHaveNotEnoughItems(itemsForGod)
            if (godsToAdd.isNotEmpty() && itemsToAdd.isNotEmpty()) {
                if (godsToAdd.all { god -> itemsToAdd.all { itemsForGod[god]!!.contains(it) } }) {
                    failCounter = purgeImpossibleOption(itemsForGod)
                } else {
                    val itemToAdd = itemsToAdd.first()
                    val godsWithoutItem = godsToAdd.filter {
                        itemsForGod[it]!!.size < HOW_MANY_ITEMS && !itemsForGod[it]!!.contains(itemToAdd)
                    }
                    val godToAdd = godsWithoutItem.firstOrNull()
                    godToAdd?.let {
                        val itemsOfGod = itemsForGod[godToAdd]!!
                        itemsOfGod.add(itemToAdd)
                        if (itemsForGod.any {
                                it.key != godToAdd && it.value.size == HOW_MANY_ITEMS && it.value.intersect(
                                    itemsOfGod.toSet()
                                ).size == HOW_MANY_ITEMS
                            }) {
                            itemsOfGod.remove(itemToAdd)
                            failCounter++
                        } else {
                            failCounter = 0
                        }
                    }
                }
                if (failCounter >= 1000) {
                    failCounter = purgeImpossibleOption(itemsForGod)
                }
            } else {
                return itemsForGod
            }
        }
        return itemsForGod
    }

    private fun purgeImpossibleOption(
        itemsForGod: Map<God, MutableSet<Item>>
    ): Int {
        itemsForGod.forEach { it.value.clear() }
        firstGodFixed(itemsForGod)
        return 0
    }

    private fun godsHaveNotEnoughItems(
        itemsForGod: Map<God, MutableSet<Item>>
    ) = God.values().toList().shuffled(random).filter {
        itemsForGod[it]!!.size < HOW_MANY_ITEMS
    }

    private fun firstGodFixed(
        itemsForGod: Map<God, MutableSet<Item>>
    ) {
        with(God.values().first()) {
            repeat(HOW_MANY_ITEMS) {
                itemsForGod[this]!!.add(Item.values()[it])
            }
        }
    }

    private fun searchItemsThatIsNotEnough(
        possibleItems: List<Item>, itemsForGod: Map<God, MutableSet<Item>>, maxValue: Int
    ): List<Item> {
        val minSize = possibleItems.minOfOrNull { item ->
            itemsForGod.filter { it.value.contains(item) }.size
        }
        return possibleItems.filter { item ->
            val size = itemsForGod.filter { it.value.contains(item) }.size
            size == minSize && size < maxValue
        }.shuffled(random)
    }

    private fun isEnoughItemsForGods(itemsForGod: Map<God, MutableSet<Item>>) =
        itemsForGod.all { it.value.size == HOW_MANY_ITEMS }


    private fun countSimilarityStats(godsWithItems: MutableList<GodsWithItems>) = godsWithItems.map { godWithItems ->
        countSimilarity(godWithItems)
    }

    private fun countGodsWithItems(itemsForGod: Map<God, Set<Item>>): MutableList<GodsWithItems> {
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
        return godsWithItems
    }

    private fun countUsabilityDiff(itemsForGod: Map<God, Set<Item>>): Int {
        val usabilityMap = itemsForGod.map {
            it.value
        }.flatten().groupBy { it.name }.map { it.value.size }
        val max = usabilityMap.maxBy { it }
//        val min = usabilityMap.minBy { it }
        val maxNumber = usabilityMap.filter { it == max }.size
//        val minNumber = usabilityMap.filter { it == min }.size
        return maxNumber
    }


    private fun countSameMax(minMax: Double, godsWithItems: MutableList<GodsWithItems>): Int =
        godsWithItems.asSequence().filter {
            countSimilarity(it).equals(minMax)
        }.map {
            listOf(it.first, it.second)
        }.flatten().groupBy {
            it.name
        }.map {
            it.value.size
        }.maxOrNull() ?: 0

    private fun countWorst(similarityStats: List<Double>, max: Double): Int = similarityStats.filter { it == max }.size


    private fun countSimilarity(godsWithItems: GodsWithItems): Double =
        godsWithItems.types.toDouble() * godsWithItems.items.toDouble()

    private fun godsWithItems(
        firstGod: God, secondGod: God, itemsMap: Map<God, Set<Item>>
    ) = GodsWithItems(
        firstGod,
        secondGod,
        firstGod.getTypes().intersect(secondGod.getTypes().toSet()).size,
        itemsMap[firstGod]!!.intersect(itemsMap[secondGod]!!.toSet()).size
    )


    data class GodsWithItems(
        val first: God, val second: God, val types: Int, val items: Int
    )

    private fun List<GodsWithItems>?.convert(): String? =
        this?.sortedBy {
            it.first.name
        }?.sortedBy {
            it.items
        }?.sortedBy {
            it.items * it.types
        }?.joinToString("\r\n") {
            it.first.name +
                    " - " +
                    it.second.name +
                    ": " +
                    it.types +
                    ", " +
                    it.items
        }


    private fun Map<God, Collection<Item>>.convert(): String {
        return this
            .map { it.key to it.value.sortedBy { item -> item.name } }
            .sortedBy { it.first.name }
            .map {
                it.first.name +
                        " - " +
                        it.second.joinToString(",") { item -> item.name }
            }.joinToString("\r\n") { it }
    }
}