package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.model.dataaccess.GameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameType
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState.NEW
import com.arkhamusserver.arkhamus.view.maker.GameSessionToGameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.CustomGameValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component


@Component
class CustomGameLogic(
    private val gameLogic: GameLogic,
    private val gameRepository: GameRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val currentUserService: CurrentUserService,
    private val gameValidator: CustomGameValidator,
    private val gameSessionToGameSessionDtoMaker: GameSessionToGameSessionDtoMaker,
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 8
        private const val DEFAULT_CULTIST_SIZE = 2
    }

    @Transactional
    fun findGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.findGameNullSafe(gameId).toDto(player)
    }

    @Transactional
    fun findUsersOpenGame(playerId: Long): GameSessionDto? {
        val player = currentUserService.getCurrentUserAccount()
        val hosted = userOfGameSessionRepository.findByUserAccountId(playerId).filter { it.host }
        hosted.sortedByDescending { it.gameCreationTimestamp }.forEach {
            val game = it.gameSession
            if (game.state == NEW) {
                return game.toDto(player)
            }
        }
        return null
    }

    @Transactional
    fun createGame(): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.createNewGameSession(
            DEFAULT_LOBBY_SIZE,
            DEFAULT_CULTIST_SIZE,
            GameType.CUSTOM
        ).also {
            val host = gameLogic.connectUserToGame(player, it, true)
            it.usersOfGameSession = listOf(host)
        }.toDto(player)
    }

    @Transactional
    fun connectToGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = gameLogic.findGameNullSafe(gameId)
        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        gameValidator.checkJoinAccess(player, game, invitedUsers)
        val connectedUser = gameLogic.connectUserToGame(player, game)
        val usersOfGameSession = game.usersOfGameSession?.let { it + connectedUser } ?: emptyList()
        game.usersOfGameSession = usersOfGameSession
        return game.toDto(player)
    }

    @Transactional
    fun updateLobby(gameId: Long, gameSessionDto: GameSessionDto): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = gameLogic.findGameNullSafe(gameId)
        gameValidator.checkUpdateAccess(player, game, gameSessionDto)
        gameSessionToGameSessionDtoMaker.merge(game, gameSessionDto)
        gameRepository.save(game)
        return game.toDto(player)
    }

    fun start(game: GameSession): GameSessionDto? {
        val player = currentUserService.getCurrentUserAccount()

        val invitedUsers = game.id?.let {
            userOfGameSessionRepository.findByGameSessionId(it)
        } ?: throw IllegalStateException("users of game session")

        gameValidator.checkStartAccess(player, game, invitedUsers)
        gameLogic.startGame(game)
        gameLogic.updateInvitedUsersInfoOnGameStart(game, invitedUsers, DEFAULT_CULTIST_SIZE)
        return game.toDto(player)
    }

    private fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionToGameSessionDtoMaker.toDto(this, currentPlayer)
}


