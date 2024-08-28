package com.arkhamusserver.arkhamus.view.dto.netty.request.banvote

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class VoteSpotCastRequestMessage(
    var voteSpotId: Long,
    var targetUserId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)