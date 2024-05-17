package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

data class ExecutedAction(
    var actionId: Long,
    var executedSuccessfully: Boolean,
    var requestType: String,
)