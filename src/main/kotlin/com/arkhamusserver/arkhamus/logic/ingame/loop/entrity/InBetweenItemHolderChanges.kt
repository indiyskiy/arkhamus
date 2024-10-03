package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class InBetweenItemHolderChanges(
    val item: Item,
    val number: Int,
    val userId: Long,
    val itemHolderChangeType: ItemHolderChangeType
)
