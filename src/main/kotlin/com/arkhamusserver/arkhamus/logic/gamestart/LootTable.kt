package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class LootTable(
    var size: Double,
    var lootRaws: List<LootRaw>
)

data class LootRaw(
    var item: Item,
    var weight: Int,
    var weightSize: Double,
)