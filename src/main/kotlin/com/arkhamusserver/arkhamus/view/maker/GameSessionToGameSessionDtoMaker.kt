package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import com.arkhamusserver.arkhamus.view.dto.RoleDto
import org.springframework.stereotype.Component

@Component
class GameSessionToGameSessionDtoMaker {
    fun toDto(gameSession: GameSession, currentPlayer: UserAccount): GameSessionDto {
        val currentUserRole =
            gameSession.usersOfGameSession?.firstOrNull { it.userAccount.id == currentPlayer.id }?.roleInGame
        val cultist = currentUserRole == RoleTypeInGame.CULTIST
        return GameSessionDto().apply {
            id = gameSession.id
            state = gameSession.state
            gameType = gameSession.gameType
            lobbySize = gameSession.lobbySize
            numberOfCultists = gameSession.numberOfCultists
            god = when (gameSession.state) {
                GameState.NEW -> null
                GameState.IN_PROGRESS -> if (cultist) gameSession.god else null
                GameState.FINISHED -> gameSession.god
            }
            roleDtos = gameSession.usersOfGameSession?.map {
                RoleDto().apply {
                    this.userId = it.userAccount.id
                    this.userName = it.userAccount.nickName
                    this.userRole = when (gameSession.state) {
                        GameState.NEW -> null
                        GameState.IN_PROGRESS -> if (cultist) it.roleInGame else RoleTypeInGame.INVESTIGATOR
                        GameState.FINISHED -> it.roleInGame
                    }
                }
            }
            gameSession.level?.let { level ->
                this.level = LevelDto().apply {
                    this.levelId = level.id
                    this.version = level.version
                }
            }
        }

    }

    fun merge(game: GameSession, level: Level?, gameSessionDto: GameSessionDto) {
        game.lobbySize = gameSessionDto.lobbySize
        game.numberOfCultists = gameSessionDto.numberOfCultists
        game.level = level
    }
}