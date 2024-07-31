package com.arkhamusserver.arkhamus.view.dto.netty.request.quest

import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

class QuestStepCompleteRequestMessage(
    var questStepId: Long,
    type: String,
    baseRequestData: BaseRequestData
) : NettyBaseRequestMessage(baseRequestData, type)