package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class AltarContent(
    var altarId: Long = 0L,
    var itemId: Int = 0,
    var itemNumberMax: Int = 0,
    var itemNumberNow: Int = 0,
)