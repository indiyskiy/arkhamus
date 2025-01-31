package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity

data class InGameUserVoteSpot(
    override var id: String,
    override var gameId: Long,
    var voteSpotId: Long,
    var userId: Long,
    var votesForUserIds: MutableList<Long> = mutableListOf(),
) : InGameEntity