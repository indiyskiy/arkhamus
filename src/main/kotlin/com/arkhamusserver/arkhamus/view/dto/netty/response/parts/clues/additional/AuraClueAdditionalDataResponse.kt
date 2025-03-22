package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.ClueState

data class AuraClueAdditionalDataResponse(
    //sparks
    val showSparks: Boolean,
    val distancePercentage: Int,
    //shadow
    val shadowState: ClueState,
    val shadowPoint: SimpleCoordinates?,
    //well
    val wellState: ClueState
) : AdditionalClueDataResponse