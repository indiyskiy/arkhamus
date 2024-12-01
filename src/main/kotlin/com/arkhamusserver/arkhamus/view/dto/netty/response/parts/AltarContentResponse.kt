package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class AltarContentResponse(
    var altarId: Long = 0L,
    var itemId: Int? = null,
    var itemNumberMax: Int = 0,
    var itemNumberNow: Int = 0,
){
    constructor(
        altarContent: AltarContent
    ) : this(
        altarContent.altarId,
        altarContent.item?.id,
        altarContent.itemNumberMax,
        altarContent.itemNumberNow
    )
}