package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MockResponseSendingLoopManager: ResponseSendingLoopManager {

    val collectedResponses = mutableMapOf<Long, List<NettyResponse>>()

    override fun addResponses(responses: List<NettyResponse>, gameId: Long) {
        // do nothing for now
        collectedResponses[gameId] = collectedResponses.getOrElse(gameId) { emptyList() } + responses
        println("collectedResponses=$collectedResponses!")
    }

    fun cleanUp() {
        collectedResponses.clear()
    }
}