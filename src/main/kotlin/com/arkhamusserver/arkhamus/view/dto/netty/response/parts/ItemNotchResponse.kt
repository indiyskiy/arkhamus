package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.ItemNotch
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class ItemNotchResponse(
    var itemId: Int = 0,
    var altarId: Long = 0L,
    var gameTimeStart: Long = 0,
    var gameTimeEnd: Long = 0,
) {
    constructor(
        itemNotch: ItemNotch
    ) : this(
        itemNotch.item?.id ?: Item.PURE_NOTHING.id,
        itemNotch.altarId,
        itemNotch.gameTimeStart,
        itemNotch.gameTimeEnd,
    )
}