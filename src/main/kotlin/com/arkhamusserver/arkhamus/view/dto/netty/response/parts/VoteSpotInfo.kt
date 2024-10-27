package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState

data class VoteSpotInfo(
    var costValue: Int? = null,
    var costItem: Int? = null,
    var state: VoteSpotState = VoteSpotState.WAITING_FOR_PAYMENT,
    var usersWithBanStates: List<UserWithBanState> = emptyList(),
)