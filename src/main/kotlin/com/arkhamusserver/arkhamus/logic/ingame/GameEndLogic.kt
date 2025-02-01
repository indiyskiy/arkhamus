package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class GameEndLogic(
    private val inRamGameRepository: InRamGameRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val timeEventHandler: TimeEventHandler,
    private val activityHandler: ActivityHandler,
    private val userStatusService: UserStatusService
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(GameEndLogic::class.java)
    }

    @Transactional
    fun endTheGame(
        game: InRamGame,
        users: Map<Long, InGameGameUser>,
        gameEndReason: GameEndReason,
        timeLeft: Long? = null
    ) {
        if (game.state == GameState.FINISHED.name || game.state == GameState.GAME_END_SCREEN.name) {
            return
        }
        saveGameState(game, gameEndReason)
        val gameSession = endGameSession(game, gameEndReason)
        setWinnersLosers(gameSession, gameEndReason, users)
        createEndOfGameTimeEvent(game, timeLeft = timeLeft)
        saveActivities(game.inGameId())
        users.values.forEach { user ->
            if (!user.leftTheGame) {
                userStatusService.updateUserStatus(user.inGameId(), UserState.ONLINE, true)
            }
        }
    }

    private fun saveActivities(gameId: Long) {
        activityHandler.saveAll(gameId)
    }

    private fun createEndOfGameTimeEvent(
        game: InRamGame,
        timeLeft: Long? = null
    ) {
        logger.info("creating end of the game event")
        timeEventHandler.createEvent(
            game,
            InGameTimeEventType.GAME_END,
            timeLeft = timeLeft
        )
    }

    private fun setWinnersLosers(
        gameSession: GameSession,
        gameEndReason: GameEndReason,
        users: Map<Long, InGameGameUser>
    ) {
        logger.info("set winners and losers")
        val databaseUsers = userOfGameSessionRepository.findByGameSessionIdAndLeftTheLobby(gameSession.id!!)
        logger.info("found ${databaseUsers.size} users of the game")
        setWonOrLoose(gameEndReason, users, databaseUsers)
        userOfGameSessionRepository.saveAll(databaseUsers)
    }

    private fun saveGameState(
        game: InRamGame,
        gameEndReason: GameEndReason
    ) {
        logger.info("saving end game state")
        game.state = GameState.GAME_END_SCREEN.name
        game.gameEndReason = gameEndReason.name
        inRamGameRepository.save(game)
    }

    private fun endGameSession(
        game: InRamGame,
        gameEndReason: GameEndReason
    ): GameSession {
        logger.info("ending game session")
        val gameSession = gameSessionRepository.findById(game.gameId).get()
        gameSession.state = GameState.GAME_END_SCREEN
        gameSession.gameEndReason = gameEndReason
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)

        return gameSession
    }

    private fun setWonOrLoose(
        gameEndReason: GameEndReason,
        inGameUsers: Map<Long, InGameGameUser>,
        databaseUsers: List<UserOfGameSession>
    ) {
        when (gameEndReason) {
            GameEndReason.GOD_AWAKEN -> cultistsWon(databaseUsers)
            GameEndReason.EVERYBODY_MAD -> cultistsWon(databaseUsers)
            GameEndReason.RITUAL_SUCCESS -> investigatorsWon(databaseUsers)
            GameEndReason.CULTISTS_BANNED -> investigatorsWon(databaseUsers)
            GameEndReason.ABANDONED -> noOneWon(databaseUsers)
        }
        databaseUsers.forEach { databaseUser ->
            val inGameUser = inGameUsers[databaseUser.userAccount.id]
            inGameUser?.let { userNullSafe -> userNullSafe.won = databaseUser.won }
            logger.info("in-game user ${inGameUser?.inGameId() ?: "null"} won? ${inGameUser?.won ?: "null"}")
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
    fun endTheGameCompletely(game: InRamGame) {
        logger.info("ending the game completely ${game.gameId}")
        game.state = GameState.FINISHED.name
        inRamGameRepository.save(game)

        val gameSession = gameSessionRepository.findById(game.gameId).get()
        gameSession.state = GameState.FINISHED
        gameSessionRepository.save(gameSession)
    }
}