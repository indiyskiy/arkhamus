package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState

data class VoteSpotInfoResponse(
    var costValue: Int,
    var costItemId: Int,
    var state: VoteSpotState = VoteSpotState.WAITING_FOR_PAYMENT,
    var usersWithBanStates: List<UserWithBanState> = emptyList(),
){
    constructor(
        voteSpotInfo: VoteSpotInfo
    ) : this(
        voteSpotInfo.costValue,
        voteSpotInfo.costItem.id,
        voteSpotInfo.state,
        voteSpotInfo.usersWithBanStates
    )
}