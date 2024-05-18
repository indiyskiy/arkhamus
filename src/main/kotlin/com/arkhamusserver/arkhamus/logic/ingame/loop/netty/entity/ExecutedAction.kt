package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData

data class ExecutedAction(
    var actionId: Long,
    var executedSuccessfully: Boolean,
    var requestType: String,
    var requestProcessData: RequestProcessData? = null
)