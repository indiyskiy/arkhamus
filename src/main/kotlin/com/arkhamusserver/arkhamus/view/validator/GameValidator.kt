package com.arkhamusserver.arkhamus.view.validator

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType.CUSTOM
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType.SINGLE
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.validator.utils.assertNotNull
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component

@Component
class GameValidator {

    companion object {
        const val RELATED_OBJECT = "Game"
    }

    fun checkJoinAccess(player: UserAccount, game: GameSession) {
        checkGameTypeCustom(game)
        checkStateNew(game)
        assertTrue(
            game.usersOfGameSession.all { it.userAccount.id != player.id || it.leftTheLobby },
            "this user ${player.nickName} is invited already",
            RELATED_OBJECT
        )
        assertTrue(
            (game.usersOfGameSession.size) < (game.gameSessionSettings.lobbySize),
            "lobby is full",
            RELATED_OBJECT
        )
    }

    fun checkStartAccess(player: UserAccount, game: GameSession, invitedUsers: List<UserOfGameSession>) {
        checkStateNew(game)
        checkIsHost(invitedUsers, player)
        val lobbySize = game.gameSessionSettings.lobbySize
        val numberOfCultistsSize = game.gameSessionSettings.numberOfCultists
        checkLobbySize(lobbySize, numberOfCultistsSize, game.gameType)
        if (game.gameType == CUSTOM) {
            assertTrue(
                invitedUsers.size > numberOfCultistsSize,
                "invited less or equal number (${invitedUsers.size}) users than cultists needed (${numberOfCultistsSize})",
                RELATED_OBJECT
            )
        }
        checkLevelSelected(game.gameSessionSettings.level)
    }

    fun checkUpdateAccess(player: UserAccount, game: GameSession, gameSessionSettingsDto: GameSessionSettingsDto) {
        checkStateNew(game)
        checkIsHost(game.usersOfGameSession.filter { !it.leftTheLobby }, player)
        val lobbySize = gameSessionSettingsDto.lobbySize
        val numberOfCultistsSize = gameSessionSettingsDto.numberOfCultists
        checkLobbySize(lobbySize, numberOfCultistsSize, game.gameType)
    }

    private fun checkGameTypeCustom(game: GameSession) {
        assertTrue(
            game.gameType == CUSTOM,
            "invalid mame type ${game.gameType}, should be $CUSTOM",
            RELATED_OBJECT
        )
    }

    private fun checkLobbySize(lobbySize: Int, numberOfCultistsSize: Int, gameType: GameType) {
        if (gameType == SINGLE) {
            assertTrue(
                lobbySize == 1,
                "lobby size for $SINGLE game must be 1",
                RELATED_OBJECT
            )
            assertTrue(
                numberOfCultistsSize <= 1,
                "number of cultists for $SINGLE game must be 0 or 1",
                RELATED_OBJECT
            )
            assertTrue(
                numberOfCultistsSize >= 0,
                "number of cultists must be positive",
                RELATED_OBJECT
            )
        } else {
            assertTrue(
                lobbySize > 1,
                "lobby size for $CUSTOM game must be >1",
                RELATED_OBJECT
            )
            assertTrue(
                numberOfCultistsSize > 0,
                "number of cultists for $CUSTOM game must be >0",
                RELATED_OBJECT
            )
            assertTrue(
                lobbySize > numberOfCultistsSize,
                "lobby size(${lobbySize}) must be > than number of cultists(${numberOfCultistsSize})",
                RELATED_OBJECT
            )
        }
    }

    private fun checkStateNew(game: GameSession) {
        assertTrue(
            game.state == GameState.NEW,
            "game must be in state ${GameState.NEW}, now state is ${game.state}",
            RELATED_OBJECT
        )
    }

    private fun checkIsHost(
        invitedUsers: List<UserOfGameSession>,
        player: UserAccount
    ) {
        assertNotNull(
            invitedUsers,
            "list of users is empty",
            RELATED_OBJECT
        )
        assertTrue(
            invitedUsers.firstOrNull { it.host }?.userAccount?.id?.let {
                it == player.id
            } == true,
            "user ${player.id} is not a host of the game",
            RELATED_OBJECT
        )
    }

    private fun checkLevelSelected(level: Level?) {
        assertNotNull(
            level,
            "Level is not selected",
            RELATED_OBJECT
        )
    }
}