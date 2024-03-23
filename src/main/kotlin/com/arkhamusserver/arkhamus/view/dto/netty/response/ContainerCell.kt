package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class ContainerCell(
    var itemId: Long = Item.PURE_NOTHING.getId(),
    var number: Long = 0L
)