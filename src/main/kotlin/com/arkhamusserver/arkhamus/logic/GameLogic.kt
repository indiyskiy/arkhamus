package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.dataaccess.GameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import org.springframework.stereotype.Component

@Component
class GameLogic(
    private val gameRepository: GameRepository,
    private val userAccountRepository: UserAccountRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository
) {

    companion object {
        private const val DEFAULT_LOBBY_SIZE = 8
    }


    fun findGame(gameId: Long): GameSession? {
        return findGameNullSafe(gameId)
    }

    fun findUsersOpenGame(playerId: Long): GameSession? {
        val hosted = userOfGameSessionRepository.findByUserAccountId(playerId).filter { it.host ?: false }
        val gameSessions: List<GameSession> = hosted.mapNotNull { it.gameSession }
        return gameSessions.firstOrNull { it.state == GameState.NEW }
    }

    fun createGame(playerId: Long): GameSession? {
        val player = findPlayerNullSafe(playerId)
        return player.id?.let {
            createNewGameSession()
        }.also {
            connectUserToGame(player, it, true)
        }
    }

    fun connectToGame(playerId: Long, gameId: Long): GameSession {
        val player = findPlayerNullSafe(playerId)
        val game = findGameNullSafe(gameId)
        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        checkJoinAccess(player, game, invitedUsers)
        connectUserToGame(player, game)
        return game
    }

    fun start(playerId: Long, gameId: Long): GameSession {
        val player = findPlayerNullSafe(playerId)
        val game = findGameNullSafe(gameId)
        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        checkStartAccess(player, game, invitedUsers)
        game.state = GameState.IN_PROGRESS
        gameRepository.save(game)
        return game
    }

    private fun findGameNullSafe(gameId: Long): GameSession =
        gameRepository.findById(gameId).orElseThrow {
            RuntimeException("wtf?")
        }

    private fun findPlayerNullSafe(playerId: Long): UserAccount =
        userAccountRepository.findById(playerId).orElseThrow {
            RuntimeException("wtf?")
        }

    private fun connectUserToGame(
        player: UserAccount?,
        game: GameSession?,
        host: Boolean = false
    ) {
        val userOfGameSession = UserOfGameSession().apply {
            this.userAccount = player
            this.gameSession = game
            this.host = host
        }
        userOfGameSessionRepository.save(userOfGameSession)
    }

    private fun checkJoinAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        assert(game.state == GameState.NEW)
        assert(!invitedUsers.any { it.id == player.id })
        assert(invitedUsers.size < (game.lobbySize ?: 0))
    }

    private fun checkStartAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        assert(game.state == GameState.NEW)
        assert(invitedUsers.first { it.host ?: false }.userAccount?.id == player.id)
    }

    private fun createNewGameSession() =
        GameSession(
            state = GameState.NEW,
            lobbySize = DEFAULT_LOBBY_SIZE
        ).apply {
            gameRepository.save(this)
        }

}