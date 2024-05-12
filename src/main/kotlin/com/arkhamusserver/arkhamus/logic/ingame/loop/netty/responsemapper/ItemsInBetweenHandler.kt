package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.ItemHolderChangeType
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.springframework.stereotype.Component

@Component
class ItemsInBetweenHandler {
    fun applyInBetween(
        containerCells: List<ContainerCell>,
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<ContainerCell> {
        val myChanges = inBetweenItemHolderChanges.filter { it.userId == userId }
        val toAdd = myChanges.filter { it.itemHolderChangeType == ItemHolderChangeType.TAKE }
        val toRemove = myChanges.filter { it.itemHolderChangeType == ItemHolderChangeType.LOOSE }
        val result = containerCells.toMutableList()
        toAdd.forEach {
            result.add(ContainerCell().apply {
                itemId = it.item.id
                number = it.number
            })
        }
        toRemove.forEach { remove ->
            val index = result.indexOfLast {
                it.itemId == remove.item.id
            }
            if (index != -1) {
                val valueBefore = result[index].number
                val valueAfter = if (valueBefore > remove.number) {
                    valueBefore - remove.number
                } else {
                    0
                }
                result[index] = ContainerCell().apply {
                    itemId = remove.item.id
                    number = valueAfter
                }
            }
        }
        return result

    }

}
