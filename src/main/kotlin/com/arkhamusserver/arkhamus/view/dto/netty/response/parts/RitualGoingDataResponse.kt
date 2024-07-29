package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class RitualGoingDataResponse(
    var godId: Int? = null,
    var altarsContent: List<AltarContent> = emptyList(),

    var currentItemId: Int = 0,
    var currentItemMax: Int = 0,
    var currentItemInside: Int = 0,

    var gameTimeStart: Long = 0L,
    var gameTimeEnd: Long = 0L,
    var gameTimeNow: Long = 0L,
    var gameTimeItemsNotches: List<ItemNotch> = emptyList(),

    var userIdsInRitual: List<Long> = emptyList()
)