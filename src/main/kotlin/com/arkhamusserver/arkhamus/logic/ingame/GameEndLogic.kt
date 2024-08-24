package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
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
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class GameEndLogic(
    private val redisGameRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val timeEventHandler: TimeEventHandler
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(GameEndLogic::class.java)
    }

    @Transactional
    fun endTheGame(
        game: RedisGame,
        users: Map<Long, RedisGameUser>,
        gameEndReason: GameEndReason,
        timeLeft: Long? = null
    ) {
        if (game.state == GameState.FINISHED.name || game.state == GameState.GAME_END_SCREEN.name) {
            logger.info("already finished")
            return
        }
        saveGameState(game, gameEndReason)
        val gameSession = endGameSession(game, gameEndReason)
        setWinnersLosers(gameSession, gameEndReason, users)
        createEndOfGameTimeEvent(game, timeLeft = timeLeft)
    }

    private fun createEndOfGameTimeEvent(
        game: RedisGame,
        timeLeft: Long? = null
    ) {
        logger.info("creating end of the game event")
        timeEventHandler.createEvent(
            game,
            RedisTimeEventType.GAME_END,
            timeLeft = timeLeft
        )
    }

    private fun setWinnersLosers(
        gameSession: GameSession,
        gameEndReason: GameEndReason,
        users: Map<Long, RedisGameUser>
    ) {
        logger.info("set winners and losers")
        val databaseUsers = userOfGameSessionRepository.findByGameSessionId(gameSession.id!!)
        logger.info("found ${databaseUsers.size} users of the game")
        setWonOrLoose(gameEndReason, users, databaseUsers)
        userOfGameSessionRepository.saveAll(databaseUsers)
    }

    private fun saveGameState(
        game: RedisGame,
        gameEndReason: GameEndReason
    ) {
        logger.info("saving end game state")
        game.state = GameState.GAME_END_SCREEN.name
        game.gameEndReason = gameEndReason.name
        redisGameRepository.save(game)
    }

    private fun endGameSession(
        game: RedisGame,
        gameEndReason: GameEndReason
    ): GameSession {
        logger.info("ending game session")
        val gameSession = gameSessionRepository.findById(game.gameId!!).get()
        gameSession.state = GameState.GAME_END_SCREEN
        gameSession.gameEndReason = gameEndReason
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)

        return gameSession
    }

    private fun setWonOrLoose(
        gameEndReason: GameEndReason, redisUsers: Map<Long, RedisGameUser>, databaseUsers: List<UserOfGameSession>
    ) {
        when (gameEndReason) {
            GameEndReason.GOD_AWAKEN -> cultistsWon(databaseUsers)
            GameEndReason.EVERYBODY_MAD -> cultistsWon(databaseUsers)
            GameEndReason.RITUAL_SUCCESS -> investigatorsWon(databaseUsers)
            GameEndReason.ABANDONED -> noOneWon(databaseUsers)
        }
        databaseUsers.forEach { databaseUser ->
            val redisUser = redisUsers[databaseUser.userAccount.id]
            redisUser?.let { userNullSafe -> userNullSafe.won = databaseUser.won }
            logger.info("redis user ${redisUser?.id ?: "null"} won? ${redisUser?.won ?: "null"}")
        }
    }

    private fun cultistsWon(users: List<UserOfGameSession>) {
        users.forEach { user ->
            when (user.roleInGame) {
                RoleTypeInGame.CULTIST -> {
                    user.won = true
                }

                RoleTypeInGame.INVESTIGATOR -> {
                    user.won = false
                }

                RoleTypeInGame.NEUTRAL -> {
                    user.won = false
                }

                null -> {
                    user.won = false
                }
            }
            logUserWinStatus(user)
        }
    }

    private fun investigatorsWon(users: List<UserOfGameSession>) {
        users.forEach { user ->
            when (user.roleInGame) {
                RoleTypeInGame.CULTIST -> {
                    user.won = false
                }

                RoleTypeInGame.INVESTIGATOR -> {
                    user.won = true
                }

                RoleTypeInGame.NEUTRAL -> {
                    user.won = false
                }

                null -> {
                    user.won = false
                }
            }
            logUserWinStatus(user)
        }
    }

    private fun noOneWon(users: List<UserOfGameSession>) {
        users.forEach { user ->
            user.won = null
            logUserWinStatus(user)
        }
    }


    private fun logUserWinStatus(user: UserOfGameSession) {
        logger.info("user ${user.userAccount.id} won? ${user.won ?: "null"}")
    }

    @Transactional
    fun endTheGameCompletely(game: RedisGame) {
        logger.info("ending the game completely ${game.gameId}")
        game.state = GameState.FINISHED.name
        redisGameRepository.save(game)

        val gameSession = gameSessionRepository.findById(game.gameId!!).get()
        gameSession.state = GameState.FINISHED
        gameSessionRepository.save(gameSession)
    }
}