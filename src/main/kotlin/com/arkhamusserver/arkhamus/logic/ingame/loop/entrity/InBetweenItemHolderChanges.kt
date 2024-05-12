package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class InBetweenItemHolderChanges(
    val item: Item,
    val number: Long,
    val userId: Long,
    val itemHolderChangeType: ItemHolderChangeType
)
