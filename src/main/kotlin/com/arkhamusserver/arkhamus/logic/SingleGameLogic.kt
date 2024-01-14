package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameType
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.view.maker.GameSessionToGameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.SingleGameValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component


@Component
class SingleGameLogic(
    private val gameLogic: GameLogic,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val currentUserService: CurrentUserService,
    private val gameValidator: SingleGameValidator,
    private val gameSessionToGameSessionDtoMaker: GameSessionToGameSessionDtoMaker,
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 1
        private const val DEFAULT_CULTIST_SIZE = 1
    }

    @Transactional
    fun createGame(): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.createNewGameSession(
            DEFAULT_LOBBY_SIZE,
            DEFAULT_CULTIST_SIZE,
            GameType.SINGLE
        ).also {
            gameLogic.connectUserToGame(player, it, true)
        }.toDto(player)
    }

    @Transactional
    fun start(game: GameSession): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()

        val invitedUsers = game.id?.let {
            userOfGameSessionRepository.findByGameSessionId(it)
        }?: throw IllegalStateException("users of game session")

        gameValidator.checkStartAccess(player, game, invitedUsers)
        gameLogic.startGame(game)
        gameLogic.updateInvitedUsersInfoOnGameStart(
            game,
            invitedUsers,
            DEFAULT_CULTIST_SIZE
        )
        return game.toDto(player)
    }

    fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionToGameSessionDtoMaker.toDto(this, currentPlayer)

}


