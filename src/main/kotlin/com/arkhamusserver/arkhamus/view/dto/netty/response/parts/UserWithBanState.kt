package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.BanState

data class UserWithBanState(
    var userId: Long,
    var banState: BanState,
    var voteCount: Int,
    var currentUserVoteCast: Boolean,
)