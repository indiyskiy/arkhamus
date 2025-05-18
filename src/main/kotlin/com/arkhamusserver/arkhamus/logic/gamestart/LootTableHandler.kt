package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.ContainerTag
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component
import kotlin.math.roundToInt
import kotlin.random.Random

@Component
class LootTableHandler {

    companion object {
        val random: Random = Random(System.currentTimeMillis())
    }

    fun generateLoot(tags: Set<ContainerTag>): List<InventoryCell> {
        val lootTable = createLootTable(tags)
        return lootByTable(lootTable)
    }


    private fun lootByTable(
        table: LootTable,
    ): List<InventoryCell> {
        val size = random.nextDouble(table.size).roundToInt() + 1
        val totalWeight = table.lootRaws.sumOf { it.weight }
        if (totalWeight < 1 || size < 1) return emptyList()
        val map = (1..size).mapNotNull {
            randomWithWeight(totalWeight, table)
        }.filter { it.second > 0 }
            .groupBy { it.first }
            .map { pair ->
                pair.key to pair.value.sumOf { it.second }
            }.map {
                InventoryCell(it.first, it.second)
            }
        return map
    }

    private fun randomWithWeight(
        totalWeight: Int,
        table: LootTable
    ): Pair<Item, Int>? {
        val randomValue = random.nextInt(totalWeight)
        var cumulativeWeight = 0
        table.lootRaws.forEach { raw ->
            cumulativeWeight += raw.weight
            if (randomValue < cumulativeWeight) {
                return raw.item to (random.nextDouble(raw.weightSize).roundToInt() + 1)
            }
        }
        return null
    }

    private fun createLootTable(tags: Set<ContainerTag>): LootTable {
        val defaultLootTable = defaultLootTable()
        tags.forEach {
            val multiplierLootTable = multiplierLootTable(it)
            multiply(defaultLootTable, multiplierLootTable)
        }
        defaultLootTable.lootRaws = filterRaws(defaultLootTable.lootRaws)
        return defaultLootTable
    }

    private fun filterRaws(raws: List<LootRaw>): List<LootRaw> =
        raws.filter {
            it.weight > 0 &&
                    it.weightSize > 0.5 &&
                    it.item.itemType != ItemType.TECH_TYPE
        }

    private fun multiply(target: LootTable, multiplier: LootTable): List<LootRaw> {
        target.size = target.size * multiplier.size
        target.lootRaws.forEach { raw ->
            val multiplierRaw = multiplier.lootRaws.firstOrNull { it.item == raw.item }
            if (multiplierRaw != null) {
                raw.weight = raw.weight * multiplierRaw.weight
                raw.weightSize = raw.weightSize * multiplierRaw.weightSize
            }
        }
        return target.lootRaws
    }

    private fun multiplierLootTable(tag: ContainerTag): LootTable {
        return when (tag) {
            ContainerTag.MAGIC -> magicLootTable()
            ContainerTag.SCIENCE -> scienceLootTable()
            ContainerTag.FANCY -> fancyLootTable()
            ContainerTag.ROUGH -> roughLootTable()
            ContainerTag.ABUNDANT -> abundantLootTable()
            ContainerTag.SCARCE -> scarceLootTable()
        }
    }

    private fun roughLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = Item.values().map {
            when (it.itemType) {
                ItemType.LOOT -> LootRaw(it, 200, 1.0)
                ItemType.RARE_LOOT -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_LOOT -> LootRaw(it, 100, 1.0)
                ItemType.USEFUL_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.ADVANCED_USEFUL_ITEM -> LootRaw(it, 1, 1.0)
                ItemType.ADVANCED_CULTIST_ITEM -> LootRaw(it, 1, 1.0)
                else -> LootRaw(Item.PURE_NOTHING, 0, 0.0)
            }
        }
        return LootTable(1.0, defaultLootRaws)
    }

    private fun abundantLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = Item.values().map {
            LootRaw(it, 1, 2.0)
        }
        return LootTable(1.5, defaultLootRaws)
    }

    private fun scarceLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = Item.values().map {
            LootRaw(it, 1, 0.5)
        }
        return LootTable(0.7, defaultLootRaws)
    }

    private fun fancyLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = Item.values().map {
            when (it.itemType) {
                ItemType.LOOT -> LootRaw(it, 1, 1.0)
                ItemType.RARE_LOOT -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_LOOT -> LootRaw(it, 1, 1.0)
                ItemType.USEFUL_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.ADVANCED_USEFUL_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.ADVANCED_CULTIST_ITEM -> LootRaw(it, 10, 1.0)
                else -> LootRaw(Item.PURE_NOTHING, 0, 0.0)
            }
        }
        return LootTable(1.0, defaultLootRaws)
    }

    private fun magicLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = listOf(
            LootRaw(Item.SAINT_QUARTZ, 100, 1.0),
            LootRaw(Item.CRYSTALLIZED_BLOOD, 100, 1.0),
            LootRaw(Item.ELDER_SIGN, 100, 1.0),
            LootRaw(Item.CORRUPTED_TOPAZ, 100, 1.0),
            LootRaw(Item.BLACK_STONE, 100, 1.0),
            LootRaw(Item.BOOK, 100, 1.0),
            LootRaw(Item.CLOCK, 100, 1.0),
            LootRaw(Item.EYE, 100, 1.0),
            LootRaw(Item.SOUL_STONE, 100, 1.0),
            LootRaw(Item.DARK_ESSENCE, 100, 1.0),
        )
        return LootTable(1.0, defaultLootRaws)
    }

    private fun scienceLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = listOf(
            LootRaw(Item.HIGGS_BOSON, 100, 1.0),
            LootRaw(Item.RAGS, 100, 1.0),
            LootRaw(Item.BLIGHTING_JEWEL, 100, 1.0),
            LootRaw(Item.MASK, 100, 1.0),
            LootRaw(Item.PLATE, 100, 1.0),
            LootRaw(Item.RING, 100, 1.0),
            LootRaw(Item.SCYTHE, 100, 1.0),
            LootRaw(Item.TEAR, 100, 1.0),
            LootRaw(Item.STRANGE_BONE, 100, 1.0),
            LootRaw(Item.INNSMOUTH_WATTER, 100, 1.0),
        )
        return LootTable(1.0, defaultLootRaws)
    }

    private fun defaultLootTable(): LootTable {
        val defaultLootRaws: List<LootRaw> = Item.values().map {
            when (it.itemType) {
                ItemType.LOOT -> LootRaw(it, 100, 5.0)
                ItemType.RARE_LOOT -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_LOOT -> LootRaw(it, 100, 5.0)
                ItemType.USEFUL_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.CULTIST_ITEM -> LootRaw(it, 10, 1.0)
                ItemType.ADVANCED_USEFUL_ITEM -> LootRaw(it, 1, 1.0)
                ItemType.ADVANCED_CULTIST_ITEM -> LootRaw(it, 1, 1.0)
                else -> LootRaw(Item.PURE_NOTHING, 0, 0.0)
            }
        }
        return LootTable(3.0, defaultLootRaws)
    }

}