package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse

interface ResponseSendingLoopManager {

    fun addResponses(
        responses: List<NettyResponse>,
        gameId: Long
    )
}