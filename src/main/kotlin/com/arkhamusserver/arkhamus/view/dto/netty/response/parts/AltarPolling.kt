package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class AltarPolling(
    var voteForGods: List<VoteForGod> = emptyList(),
    var skipped: Int? = 0
)