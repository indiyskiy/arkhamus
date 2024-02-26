package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component

@Component
class OneTickTick(
    private val gameRepository: RedisGameRepository,
    ) {
    fun updateNextTick(game: RedisGame) {
        game.currentTick += 1
        game.globalTimer += ArkhamusOneTickLogic.TICK_DELTA
        gameRepository.save(game)
    }
}