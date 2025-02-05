package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class ItemNotch(
    var index: Int = 0,
    var item: Item? = null,
    var gameTimeStart: Long = 0,
    var gameTimeEnd: Long = 0,
    var altarId: Long = 0,
)