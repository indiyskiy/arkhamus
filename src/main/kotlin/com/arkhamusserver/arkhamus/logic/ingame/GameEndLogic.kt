package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RedisTimeEventHandler
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class GameEndLogic(
    private val redisGameRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val timeEventHandler: RedisTimeEventHandler
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(GameEndLogic::class.java)
    }

    fun endTheGame(
        game: RedisGame, users: Map<Long, RedisGameUser>, gameEndReason: GameEndReason
    ) {
        if (game.state == GameState.FINISHED.name) {
            logger.info("already finished")
            return
        }
        saveGameState(game, gameEndReason)
        val gameSession = endGameSession(game, gameEndReason)
        setWinersLoosers(gameSession, gameEndReason, users)
        createEndOfGameTimeEvent(game)
    }

    private fun createEndOfGameTimeEvent(game: RedisGame) {
        logger.info("creating ond of the game event")
        timeEventHandler.createDefaultEvent(
            game,
            RedisTimeEventType.GAME_END
        )
    }

    private fun setWinersLoosers(
        gameSession: GameSession,
        gameEndReason: GameEndReason,
        users: Map<Long, RedisGameUser>
    ) {
        logger.info("set winners and losers")
        val databaseUsers = userOfGameSessionRepository.findByGameSessionId(gameSession.id!!)
        setWonOrLoose(gameEndReason, users, databaseUsers)
        userOfGameSessionRepository.saveAll(databaseUsers)
    }

    private fun saveGameState(
        game: RedisGame,
        gameEndReason: GameEndReason
    ) {
        logger.info("saving game state")
        game.state = GameState.FINISHED.name
        game.gameEndReason = gameEndReason.name
        redisGameRepository.save(game)
    }

    private fun endGameSession(
        game: RedisGame,
        gameEndReason: GameEndReason
    ): GameSession {
        logger.info("ending game session")
        val gameSession = gameSessionRepository.findById(game.gameId!!).get()
        gameSession.state = GameState.FINISHED
        gameSession.gameEndReason = gameEndReason
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)
        return gameSession
    }

    private fun setWonOrLoose(
        gameEndReason: GameEndReason, users: Map<Long, RedisGameUser>, databaseUsers: List<UserOfGameSession>
    ) {
        when (gameEndReason) {
            GameEndReason.GOD_AWAKEN -> cultistsWon(databaseUsers)
            GameEndReason.EVERYBODY_MAD -> cultistsWon(databaseUsers)
            GameEndReason.RITUAL_SUCCESS -> investigatorsWon(databaseUsers)
        }
        databaseUsers.forEach {
            val user = users[it.id]
            user?.let { userNullSafe -> userNullSafe.won = it.won }
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