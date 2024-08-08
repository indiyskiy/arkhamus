package com.arkhamusserver.arkhamus.view.dto.netty.request.quest

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class TakeQuestRewardRequestMessage(
    var questRewardId: String,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)