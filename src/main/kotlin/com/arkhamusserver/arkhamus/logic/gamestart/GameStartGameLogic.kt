package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartGameLogic(
    private val gameRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameStartGameLogic::class.java)
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun createTheGame(game: GameSession) {
        game.god = God.values().random(random)
        game.state = GameState.PENDING
        gameSessionRepository.save(game)

        gameRepository.save(
            RedisGame(game.id.toString(), game.id)
        )
    }

}