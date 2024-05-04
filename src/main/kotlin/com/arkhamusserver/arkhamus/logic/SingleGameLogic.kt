package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component


@Component
class SingleGameLogic(
    private val gameLogic: GameLogic,
    private val currentUserService: CurrentUserService,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val userSkinLogic: UserSkinLogic,
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 1
        const val DEFAULT_CULTIST_SIZE = 1
    }

    @Transactional
    fun createGame(): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val oldGame = gameLogic.findCurrentUserGame(player, GameType.SINGLE)
        if (oldGame != null) {
            return oldGame.toDto(player)
        }
        return gameLogic.createNewGameSession(
            DEFAULT_LOBBY_SIZE,
            DEFAULT_CULTIST_SIZE,
            GameType.SINGLE,
            player
        ).toDto(player)
    }

    fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionDtoMaker.toDto(this, userSkinLogic.allSkinsOf(this), currentPlayer)

}


