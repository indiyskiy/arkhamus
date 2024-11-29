package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component

@Component
class ItemsInBetweenHandler {
    fun applyInBetween(
        itemsInside: List<InventoryCell>,
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        val myChanges = inBetweenItemHolderChanges.filter { it.userId == userId }
        val toAdd = myChanges.filter { it.itemHolderChangeType == ItemHolderChangeType.TAKE }
        val toRemove = myChanges.filter { it.itemHolderChangeType == ItemHolderChangeType.LOOSE }
        val result = itemsInside.toMutableList()
        toAdd.forEach {
            result.add(InventoryCell().apply {
                item = it.item
                number = it.number
            })
        }
        toRemove.forEach { remove ->
            val index = result.indexOfLast {
                it.item == remove.item
            }
            if (index != -1) {
                val valueBefore = result[index].number
                val valueAfter = if (valueBefore > remove.number) {
                    valueBefore - remove.number
                } else {
                    0
                }
                result[index] = InventoryCell().apply {
                    item = remove.item
                    number = valueAfter
                }
            }
        }
        return result

    }

}
