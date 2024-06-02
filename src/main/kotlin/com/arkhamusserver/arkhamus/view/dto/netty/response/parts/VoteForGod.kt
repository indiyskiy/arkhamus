package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

data class VoteForGod(
    var godId: Long,
    var voteCount: Int,
)