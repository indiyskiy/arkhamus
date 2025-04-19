package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.user.relations.GameEndRelationLogic
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
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
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
    private val userStatusService: UserStatusService,
    private val gameEndRelationLogic: GameEndRelationLogic
) {
    companion object {
        private val logger = LoggingUtils.getLogger<GameEndLogic>()
    }

    @Transactional
    fun endTheGame(
        game: InRamGame,
        users: Map<Long, InGameUser>,
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
        addRelations(gameSession)
        users.values.forEach { user ->
            if (!user.techData.leftTheGame) {
                userStatusService.updateUserStatus(user.inGameId(), CultpritsUserState.ONLINE, true)
            }
        }
    }

    @Transactional
    fun endTheGameCompletely(game: InRamGame) {
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "ending the game completely {}", game.gameId)
        game.state = GameState.FINISHED.name
        inRamGameRepository.save(game)

        val gameSession = gameSessionRepository.findById(game.gameId).get()
        gameSession.state = GameState.FINISHED
        gameSessionRepository.save(gameSession)
    }

    private fun addRelations(session: GameSession) {
        gameEndRelationLogic.saveGameEndedRelations(session)
    }

    private fun saveActivities(gameId: Long) {
        activityHandler.saveAll(gameId)
    }

    private fun createEndOfGameTimeEvent(
        game: InRamGame,
        timeLeft: Long? = null
    ) {
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "creating end of the game event")
        timeEventHandler.createEvent(
            game,
            InGameTimeEventType.GAME_END,
            timeLeft = timeLeft
        )
    }

    private fun setWinnersLosers(
        gameSession: GameSession,
        gameEndReason: GameEndReason,
        users: Map<Long, InGameUser>
    ) {
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "set winners and losers")
        val databaseUsers = userOfGameSessionRepository.findByGameSessionIdAndLeftTheLobby(gameSession.id!!)
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "found {} users of the game", databaseUsers.size)
        setWonOrLoose(gameEndReason, users, databaseUsers)
        userOfGameSessionRepository.saveAll(databaseUsers)
    }

    private fun saveGameState(
        game: InRamGame,
        gameEndReason: GameEndReason
    ) {
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "saving end game state")
        game.state = GameState.GAME_END_SCREEN.name
        game.gameEndReason = gameEndReason.name
        inRamGameRepository.save(game)
    }

    private fun endGameSession(
        game: InRamGame,
        gameEndReason: GameEndReason
    ): GameSession {
        LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "ending game session")
        val gameSession = gameSessionRepository.findById(game.gameId).get()
        gameSession.state = GameState.GAME_END_SCREEN
        gameSession.gameEndReason = gameEndReason
        gameSession.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameSessionRepository.save(gameSession)

        return gameSession
    }

    private fun setWonOrLoose(
        gameEndReason: GameEndReason,
        inGameUsers: Map<Long, InGameUser>,
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
            inGameUser?.let { userNullSafe -> userNullSafe.techData.won = databaseUser.won }
            val userId = inGameUser?.inGameId()?.toString() ?: "null"
            val userWon = when (inGameUser?.techData?.won) {
                true -> "true"
                false -> "false"
                null -> "null"
            }
            LoggingUtils.info(logger, LoggingUtils.EVENT_GAME_END, "in-game user {} won? {}", userId, userWon)
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
        val wonStatus = when (user.won) {
            true -> "true"
            false -> "false"
            null -> "null"
        }
        LoggingUtils.info(
            logger,
            LoggingUtils.EVENT_GAME_END,
            "user {} won? {}",
            user.userAccount.id.toString(),
            wonStatus
        )
    }
}
