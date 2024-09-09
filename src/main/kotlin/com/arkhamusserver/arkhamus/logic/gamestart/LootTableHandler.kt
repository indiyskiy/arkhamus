package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerTag
import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerTag.*
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.roundToInt
import kotlin.random.Random

@Component
class LootTableHandler {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(LootTableHandler::class.java)
        val random: Random = Random(System.currentTimeMillis())
    }

    fun generateLoot(tags: Set<ContainerTag>): MutableMap<Int, Int> {
        val lootTable = createLootTable(tags)
        val size = countSize(tags)
        return lootByTable(size, lootTable)
    }

    private fun countSize(tags: Set<ContainerTag>): Int {
        var size = 3
        tags.forEach {
            val multiplier: Double = when (it) {
                RICH -> 1.5
                POOR -> 0.5
                else -> 1.0
            }
            size = (size * multiplier).roundToInt()
        }
        return size
    }

    private fun lootByTable(
        size: Int,
        table: LootTable,
    ): MutableMap<Int, Int> {
        val totalWeight = table.lootRaws.sumOf { it.weight }
        if (totalWeight < 1 || size < 1) return mutableMapOf()
        return (1..size).map {
            randomWithWeight(totalWeight, table)
        }.filter { it.second > 0 && it.first > 0 }
            .toMap()
            .toMutableMap()
    }

    private fun randomWithWeight(
        totalWeight: Int,
        table: LootTable
    ): Pair<Int, Int> {
        val randomValue = random.nextInt(totalWeight)
        var cumulativeWeight = 0
        table.lootRaws.forEach { raw ->
            cumulativeWeight += raw.weight
            if (randomValue < cumulativeWeight) {
                return raw.item.id to (random.nextInt(raw.weightSize) + 1)
            }
        }
        return 0 to 0
    }

    private fun createLootTable(tags: Set<ContainerTag>): LootTable {
        val lootRaws: List<LootRaw> = tags.mapNotNull {
            rawByContainerTag(it)
        }.flatten().filter { it.weight > 0 && it.weightSize > 0 }
        lootRaws.forEach { lootRaw ->
            tags.forEach { tag ->
                affectRawByTag(lootRaw, tag)
            }
        }
        val lootTable = LootTable(lootRaws)
        return lootTable
    }

    private fun affectRawByTag(
        raw: LootRaw,
        tag: ContainerTag
    ) {
        when (tag) {
            SCIENCE -> {}
            MAGIC -> {}
            RICH -> {
                raw.weightSize = (raw.weightSize * 1.5).roundToInt()
            }

            POOR -> {
                raw.weightSize = (raw.weightSize / 1.5).roundToInt()
            }
        }
    }

    private fun rawByContainerTag(tag: ContainerTag): List<LootRaw>? {
        return when (tag) {
            SCIENCE -> listOf(
                LootRaw(HIGGS_BOSON, 10, 5),
                LootRaw(SCIENTIFIC_GIZMO, 1, 1)
            )

            MAGIC -> listOf(
                LootRaw(SAINT_QUARTZ, 10, 5),
                LootRaw(ELDER_SIGN, 10, 5),
                LootRaw(BOOK, 1, 1)
            )

            RICH -> listOf(
                LootRaw(CORRUPTED_TOPAZ, 1, 5),
                LootRaw(BLIGHTING_JEWEL, 1, 5),
            )
            POOR -> listOf(
                LootRaw(RAGS, 10, 5)
            )
        }
    }

}