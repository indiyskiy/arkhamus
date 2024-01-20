package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionToGameSessionDtoMaker
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component


@Component
class SingleGameLogic(
    private val gameLogic: GameLogic,
    private val currentUserService: CurrentUserService,
    private val gameSessionToGameSessionDtoMaker: GameSessionToGameSessionDtoMaker,
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 1
        const val DEFAULT_CULTIST_SIZE = 1
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

    fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionToGameSessionDtoMaker.toDto(this, currentPlayer)

}


