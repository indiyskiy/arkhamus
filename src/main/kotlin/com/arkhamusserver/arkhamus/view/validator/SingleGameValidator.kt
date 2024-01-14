package com.arkhamusserver.arkhamus.view.validator

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameType.SINGLE
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.validator.utils.assertNotNull
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component

@Component
class SingleGameValidator {

    companion object {
        val GAME_TYPE = setOf(SINGLE)
    }

    fun checkStartAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkGameType(game)
        checkStateNew(game)
        checkIsHost(invitedUsers, player)
        val lobbySize = game.lobbySize ?: 0
        val numberOfCultistsSize = game.numberOfCultists ?: 0
        checkLobbySize(lobbySize, numberOfCultistsSize)
        checkGameType(game)
    }

    private fun checkGameType(game: GameSession) {
        assertTrue(game.gameType in GAME_TYPE)
    }

    private fun checkLobbySize(lobbySize: Int, numberOfCultistsSize: Int) {
        assertTrue(lobbySize == 1)
        assertTrue(numberOfCultistsSize == 0 || numberOfCultistsSize == 1)
    }

    private fun checkStateNew(game: GameSession) {
        assertTrue(game.state == GameState.NEW)
    }

    private fun checkIsHost(
        invitedUsers: List<UserOfGameSession>?,
        player: UserAccount
    ) {
        assertNotNull(invitedUsers)
        assertTrue(invitedUsers?.first()?.userAccount?.id == player.id)
    }

}