package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

data class AuraClueAdditionalDataResponse (
    val distancePercentage: Int?,
    val pointReached: Boolean,
    val outOfRadius: Boolean,
    val targetPoint: SimpleCoordinates?
): AdditionalClueDataResponse