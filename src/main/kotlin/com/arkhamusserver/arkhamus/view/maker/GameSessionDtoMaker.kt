package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.InGameUserDto
import com.arkhamusserver.arkhamus.view.dto.RoleDto
import org.springframework.stereotype.Component

@Component
class GameSessionDtoMaker(
    val gameSessionSettingsDtoMaker: GameSessionSettingsDtoMaker
) {
    fun toDto(gameSession: GameSession, currentPlayer: UserAccount): GameSessionDto {
        val currentUserRole =
            gameSession.usersOfGameSession.firstOrNull { it.userAccount.id == currentPlayer.id }?.roleInGame
        val isCultist = currentUserRole == RoleTypeInGame.CULTIST
        return GameSessionDto().apply {
            id = gameSession.id
            state = gameSession.state
            token = gameSession.token
            gameType = gameSession.gameType

            gameSessionSettings = gameSessionSettingsDtoMaker.toDto(gameSession.gameSessionSettings)

            god = when (gameSession.state) {
                GameState.NEW -> null
                GameState.IN_PROGRESS -> if (isCultist) gameSession.god else null
                GameState.FINISHED -> gameSession.god
            }
            usersInGame = mapRolesByReceiverRole(gameSession, isCultist)
        }
    }

    private fun mapRolesByReceiverRole(
        gameSession: GameSession,
        isCultist: Boolean
    ) = gameSession.usersOfGameSession.map {
        InGameUserDto().apply {
            this.userId = it.userAccount.id
            this.userName = it.userAccount.nickName
            this.isHost = it.host
            this.role = RoleDto().apply {
                this.userRole = when (gameSession.state) {
                    GameState.NEW -> null
                    GameState.IN_PROGRESS -> if (isCultist) it.roleInGame else RoleTypeInGame.INVESTIGATOR
                    GameState.FINISHED -> it.roleInGame
                }
            }
        }
    }
}