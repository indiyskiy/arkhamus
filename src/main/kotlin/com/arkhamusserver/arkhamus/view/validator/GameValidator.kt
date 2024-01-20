package com.arkhamusserver.arkhamus.view.validator

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType.CUSTOM
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType.SINGLE
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.validator.utils.assertNotNull
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component

@Component
class GameValidator {

    fun checkJoinAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkGameTypeCustom(game)
        checkStateNew(game)
        assertTrue(
            invitedUsers.all { it.id != player.id },
            "this user ${player.nickName} is invited already"
        )
        assertTrue(invitedUsers.size < (game.lobbySize ?: 0))
    }

    fun checkStartAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkStateNew(game)
        checkIsHost(invitedUsers, player)
        val lobbySize = game.lobbySize ?: 0
        val numberOfCultistsSize = game.numberOfCultists ?: 0
        checkLobbySize(lobbySize, numberOfCultistsSize, game.gameType)
        if (game.gameType == CUSTOM) {
            assertTrue(
                invitedUsers.size > numberOfCultistsSize,
                "invited less or equal number (${invitedUsers.size}) users " +
                        "than cultists needed (${numberOfCultistsSize})"
            )
        }
        checkLevelSelected(game.level)
    }

    fun checkUpdateAccess(player: UserAccount, game: GameSession, gameSessionDto: GameSessionDto) {
        checkStateNew(game)
        checkIsHost(game.usersOfGameSession, player)
        checkSameGame(game, gameSessionDto)
        val lobbySize = gameSessionDto.lobbySize ?: 0
        val numberOfCultistsSize = gameSessionDto.numberOfCultists ?: 0
        checkLobbySize(lobbySize, numberOfCultistsSize, game.gameType)
    }

    private fun checkGameTypeCustom(game: GameSession) {
        assertTrue(game.gameType == CUSTOM)
    }

    private fun checkLobbySize(lobbySize: Int, numberOfCultistsSize: Int, gameType: GameType) {
        if (gameType == SINGLE) {
            assertTrue(lobbySize == 1)
            assertTrue(numberOfCultistsSize == 1)
        } else {
            assertTrue(lobbySize > 1)
            assertTrue(numberOfCultistsSize > 0)
            assertTrue(lobbySize > numberOfCultistsSize)
        }
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

    private fun checkLevelSelected(level: Level?) {
        assertNotNull(level)
    }
}