package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.logic.UserSkinLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.view.dto.*
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminUserGameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.maker.ingame.GodToGodDtoMaker
import org.springframework.stereotype.Component

@Component
class AdminGameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val userSkinLogic: UserSkinLogic,
    private val godMaker: GodToGodDtoMaker
) {
    fun all(): List<GameSessionDto> {
        return gameSessionRepository.findAll().map { game ->
            gameSessionDto(game)
        }
    }

    fun allForUser(userId: Long): List<AdminUserGameSessionDto> {
        val userGameSessions = userOfGameSessionRepository.findByUserAccountId(userId)
        return userGameSessions.map {
            AdminUserGameSessionDto().apply {
                this.classInGame = it.classInGame
                this.roleInGame = it.roleInGame
                this.gameSession = adminGameSessionDto(it.gameSession)
            }
        }
    }

    fun game(gameId: Long): AdminGameSessionDto {
        val game = gameSessionRepository.findById(gameId).orElse(null)
        return adminGameSessionDto(game)
    }

    private fun adminGameSessionDto(game: GameSession?): AdminGameSessionDto {
        return game?.let {
            val skins = userSkinLogic.allSkinsOf(game)
            AdminGameSessionDto().apply {
                this.id = game.id
                this.state = game.state
                this.gameType = game.gameType
                this.god = it.god?.let { godMaker.convert(it) }
                this.usersInGame = it.usersOfGameSession.map {
                    InGameUserDto().apply {
                        userId = it.userAccount.id
                        userName = it.userAccount.nickName
                        isHost = it.host
                        gameSkin = UserSkinDto().apply {
                            userId = it.userAccount.id
                            skinColor = skins[it.userAccount.id]?.skinColor
                        }
                        role = RoleDto().apply {
                            this.userRole = it.roleInGame
                            this.userClass = it.classInGame
                        }
                    }
                }
                this.gameSessionSettings = GameSessionSettingsDto(
                    lobbySize = it.gameSessionSettings.lobbySize,
                    numberOfCultists = it.gameSessionSettings.numberOfCultists,
                    level = it.gameSessionSettings.level?.let {
                        LevelDto().apply {
                            levelId = it.levelId
                            version = it.version
                            name = "tbd"
                            state = it.state?.name ?: "tbd"
                        }
                    }
                )
                this.token = it.token
            }
        } ?: AdminGameSessionDto()
    }

    private fun gameSessionDto(game: GameSession): GameSessionDto {
        val skins = userSkinLogic.allSkinsOf(game)
        return gameSessionDtoMaker.toDtoAsAdmin(game, skins)
    }

}