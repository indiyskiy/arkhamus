package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class AltarContent(
    var altarId: Long = 0L,
    var item: Item? = null,
    var itemNumberMax: Int = 0,
    var itemNumberNow: Int = 0,
)