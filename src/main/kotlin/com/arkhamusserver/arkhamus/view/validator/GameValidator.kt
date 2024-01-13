package com.arkhamusserver.arkhamus.view.validator

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.validator.utils.assertNotNull
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component

@Component
class GameValidator {
    fun checkJoinAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkStateNew(game)
        assertTrue(invitedUsers.all { it.id != player.id })
        assertTrue(invitedUsers.size < (game.lobbySize ?: 0))
    }

    fun checkStartAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkStateNew(game)
        checkIsHost(invitedUsers, player)
        val lobbySize = game.lobbySize ?: 0
        val numberOfCultistsSize = game.numberOfCultists ?: 0
        checkLobbySize(lobbySize, numberOfCultistsSize)
        assertTrue(invitedUsers.size > numberOfCultistsSize)
    }

    fun checkUpdateAccess(player: UserAccount, game: GameSession, gameSessionDto: GameSessionDto) {
        checkStateNew(game)
        checkIsHost(game.usersOfGameSession, player)
        checkSameGame(game, gameSessionDto)
        val lobbySize = gameSessionDto.lobbySize ?: 0
        val numberOfCultistsSize = gameSessionDto.numberOfCultists ?: 0
        checkLobbySize(lobbySize, numberOfCultistsSize)
    }

    private fun checkLobbySize(lobbySize: Int, numberOfCultistsSize: Int) {
        assertTrue(lobbySize > 1)
        assertTrue(numberOfCultistsSize > 0)
        assertTrue(lobbySize > numberOfCultistsSize)
    }

    private fun checkSameGame(game: GameSession, gameSessionDto: GameSessionDto) {
        checkSameGame(game.id, gameSessionDto.id)
    }

    private fun checkSameGame(gameId: Long?, gameDtoId: Long?) {
        assertTrue(gameId != null && gameDtoId != null && gameId == gameDtoId)
    }

    private fun checkStateNew(game: GameSession) {
        assertTrue(game.state == GameState.NEW)
    }

    private fun checkIsHost(
        invitedUsers: List<UserOfGameSession>?,
        player: UserAccount
    ) {
        assertNotNull(invitedUsers)
        assertTrue(invitedUsers?.first { it.userAccount.id == player.id }?.host ?: false)
    }

}