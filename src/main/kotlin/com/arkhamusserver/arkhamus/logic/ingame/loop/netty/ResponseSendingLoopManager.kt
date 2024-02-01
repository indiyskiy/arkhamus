package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component

@Component
class ResponseSendingLoopManager {
    fun addResponse(
        response: GameResponseMessage,
        tick: Long,
        game: RedisGame
    ) {
        TODO("Not yet implemented")
    }

    fun flush(tick: Long, game: RedisGame, gameId: Long) {
        TODO("Not yet implemented")
    }
}