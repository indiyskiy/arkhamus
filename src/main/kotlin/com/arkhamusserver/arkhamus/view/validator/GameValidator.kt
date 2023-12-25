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
    }

    fun checkUpdateAccess(player: UserAccount, game: GameSession, gameSessionDto: GameSessionDto) {
        checkStateNew(game)
        checkSameGame(game, gameSessionDto)
        checkIsHost(game.usersOfGameSession, player)
        assertTrue(gameSessionDto.lobbySize?.let { it > 1 } ?: false)
        assertTrue(gameSessionDto.numberOfCultists?.let { it > 0 } ?: false)
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
        assertTrue(invitedUsers?.let { it.first { it.userAccount.id == player.id }.host } ?: false)
    }

}