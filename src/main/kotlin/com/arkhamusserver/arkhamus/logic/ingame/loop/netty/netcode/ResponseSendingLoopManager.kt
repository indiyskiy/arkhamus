package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage

interface ResponseSendingLoopManager {

    fun addResponses(
        responses: List<NettyResponseMessage>,
        gameId: Long
    )
}