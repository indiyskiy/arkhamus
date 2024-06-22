package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class GameEndLogic(
    private val redisGameRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
) {
    fun endTheGame(game: RedisGame, gameEndReason: GameEndReason) {
        if (game.state == GameState.FINISHED.name) {
            return
        }
        game.state = GameState.FINISHED.name
        game.gameEndReason = gameEndReason.name
        redisGameRepository.save(game)

        val gameSession = gameSessionRepository.findById(game.gameId!!).get()
        gameSession.state = GameState.FINISHED
        gameSession.gameEndReason = gameEndReason
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)

        val users = userOfGameSessionRepository.findByGameSessionId(gameSession.id!!)
        setWonOrLoose(gameEndReason, users)
        userOfGameSessionRepository.saveAll(users)
    }

    private fun setWonOrLoose(gameEndReason: GameEndReason, users: List<UserOfGameSession>) {
        when (gameEndReason) {
            GameEndReason.GOD_AWAKEN -> cultistsWon(users)
            GameEndReason.RITUAL_SUCCESS -> investigatorsWon(users)
        }
    }

    private fun cultistsWon(users: List<UserOfGameSession>) {
        users.forEach { user ->
            when (user.roleInGame) {
                RoleTypeInGame.CULTIST -> user.won = true
                RoleTypeInGame.INVESTIGATOR -> user.won = false
                RoleTypeInGame.NEUTRAL -> user.won = false
                null -> user.won = false
            }
        }
    }

    private fun investigatorsWon(users: List<UserOfGameSession>) {
        users.forEach { user ->
            when (user.roleInGame) {
                RoleTypeInGame.CULTIST -> user.won = false
                RoleTypeInGame.INVESTIGATOR -> user.won = true
                RoleTypeInGame.NEUTRAL -> user.won = false
                null -> user.won = false
            }
        }
    }
}