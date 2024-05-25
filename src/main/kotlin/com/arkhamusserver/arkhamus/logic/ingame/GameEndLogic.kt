package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class GameEndLogic(
    private val redisGameRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    fun endTheGame(game: RedisGame, godAwaken: GameEndReason) {
        game.state = GameState.FINISHED.name
        redisGameRepository.save(game)

        val gameSession = gameSessionRepository.findById(game.gameId!!).get()
        gameSession.state = GameState.FINISHED
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)
    }
}