package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.actionmerge

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component

@Component
class ItemsMergingHandler {

    fun mergeItems(
        sortedUserInventoryNew: List<InventoryCell>,
        visibleItemsNew: List<InventoryCell>,
    ): List<InventoryCell> {
        val sum = visibleItemsNew
            .groupBy { it.item }
            .map { cell ->
                cell.key to cell.value.sumOf { it.number }
            }.toMap().toMutableMap()
        val preMappedInventory: MutableList<InventoryCell> = sortedUserInventoryNew.map {
            val currentItem = it.item
            val mappingNumber = it.number
            val trustfulNumber = sum[currentItem]
            it.item to newValueForMapping(mappingNumber, trustfulNumber?:0, sum, currentItem)
        }.map {
            InventoryCell().apply {
                this.item = it.first
                this.number = it.second
            }
        }.toMutableList()
        val sumAfterMapping = sum.filter { it.value > 0 }
        sumAfterMapping.forEach { notMappedValue ->
            val firstEmptySlot =
                preMappedInventory.indexOfFirst { it.item == Item.PURE_NOTHING || it.number == 0 }
            if (firstEmptySlot != -1) {
                preMappedInventory[firstEmptySlot] = InventoryCell().apply {
                    this.item = notMappedValue.key
                    this.number = notMappedValue.value
                }
            } else {
                preMappedInventory.add(InventoryCell().apply {
                    this.item = notMappedValue.key
                    this.number = notMappedValue.value
                })
            }
        }
        return preMappedInventory
    }

    private fun newValueForMapping(
        mappingNumber: Int,
        trustfullNumber: Int,
        sum: MutableMap<Item, Int>,
        currentItem: Item
    ): Int {
        if (currentItem == Item.PURE_NOTHING) {
            return 0
        }
        if (mappingNumber == trustfullNumber) {
            sum[currentItem] = 0
            return mappingNumber
        } else {
            if (mappingNumber > trustfullNumber) {
                sum[currentItem] = 0
                return mappingNumber
            } else {
                sum[currentItem] = trustfullNumber - mappingNumber
                return mappingNumber
            }
        }
    }
}