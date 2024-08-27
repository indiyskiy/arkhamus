package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class VoteSpotInfo(
    var costValue: Int? = null,
    var costItem: Int? = null,
    var usersWithBanStates: List<UserWithBanState> = emptyList(),
)