package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class ContainerCell(
    var itemId: Int = Item.PURE_NOTHING.id,
    var number: Long = 0L
)