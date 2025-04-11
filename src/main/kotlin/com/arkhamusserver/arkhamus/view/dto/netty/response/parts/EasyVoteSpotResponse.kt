package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState

data class EasyVoteSpotResponse(
    var voteSpotId: Long,
    var state: MapObjectState,
    var voteSpotState: VoteSpotState,
)