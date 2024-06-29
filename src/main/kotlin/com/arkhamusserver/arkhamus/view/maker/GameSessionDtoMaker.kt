package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.GameState.*
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.INVESTIGATOR
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.InGameUserDto
import com.arkhamusserver.arkhamus.view.dto.RoleDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.ingame.GodDto
import com.arkhamusserver.arkhamus.view.maker.ingame.GodToGodDtoMaker
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.format.DateTimeFormatter

@Component
class GameSessionDtoMaker(
    private val gameSessionSettingsDtoMaker: GameSessionSettingsDtoMaker,
    private val userSkinDtoMaker: UserSkinDtoMaker,
    private val godDtoMaker: GodToGodDtoMaker
) {

    companion object {
        private const val pattern = "dd.MM.yyyy HH:mm:ss"
        val formatter = DateTimeFormatter.ofPattern(pattern)
    }

    fun toDto(
        gameSession: GameSession,
        userSkins: Map<Long, UserSkinSettings>,
        currentPlayer: UserAccount
    ): GameSessionDto {
        val currentUserRole =
            gameSession.usersOfGameSession.firstOrNull { it.userAccount.id == currentPlayer.id }?.roleInGame
        val isCultist = currentUserRole == CULTIST
        return GameSessionDto().apply {
            id = gameSession.id
            state = gameSession.state
            token = gameSession.token
            gameType = gameSession.gameType
            gameSessionSettings = gameSessionSettingsDtoMaker.toDto(gameSession.gameSessionSettings)
            god = convertGod(gameSession, isCultist)
            usersInGame = mapRolesByReceiverRole(gameSession, userSkins, isCultist, currentPlayer.id!!)
        }
    }

    fun toDtoAsAdmin(
        gameSession: GameSession,
        userSkins: Map<Long, UserSkinSettings>,
    ): AdminGameSessionDto {
        return AdminGameSessionDto().apply {
            id = gameSession.id
            state = gameSession.state
            token = gameSession.token
            gameType = gameSession.gameType
            gameSessionSettings = gameSessionSettingsDtoMaker.toDto(gameSession.gameSessionSettings)
            god = convertGodAsAdmin(gameSession)
            usersInGame = mapRolesByReceiverRoleAsAdmin(gameSession, userSkins)
            creation = dateToString(gameSession.creationTimestamp)
            started = dateToString(gameSession.startedTimestamp)
            finished = dateToString(gameSession.finishedTimestamp)
        }
    }

    private fun dateToString(date: Timestamp?) =
        date?.let {
            formatter.format(it.toLocalDateTime())
        } ?: ""


    private fun convertGod(
        gameSession: GameSession,
        isCultist: Boolean
    ): GodDto? {
        return gameSession.god?.let { godNotNull ->
            when (gameSession.state) {
                NEW -> null
                IN_PROGRESS, PENDING -> if (isCultist) {
                    godDtoMaker.convert(godNotNull)
                } else {
                    null
                }

                GAME_END_SCREEN, FINISHED -> godDtoMaker.convert(godNotNull)
            }
        }
    }

    private fun convertGodAsAdmin(
        gameSession: GameSession,
    ): GodDto? {
        return gameSession.god?.let { godNotNull ->
            when (gameSession.state) {
                NEW -> null
                IN_PROGRESS, PENDING, GAME_END_SCREEN, FINISHED -> godDtoMaker.convert(godNotNull)
            }
        }
    }

    private fun mapRolesByReceiverRole(
        gameSession: GameSession,
        userSkins: Map<Long, UserSkinSettings>,
        isCultist: Boolean,
        userId: Long,
    ) = gameSession.usersOfGameSession.map {
        InGameUserDto().apply {
            this.userId = it.userAccount.id
            this.userName = it.userAccount.nickName
            this.isHost = it.host
            this.role = RoleDto().apply {
                this.userRole = when (gameSession.state) {
                    NEW -> null
                    IN_PROGRESS, PENDING -> if (isCultist) it.roleInGame else INVESTIGATOR
                    GAME_END_SCREEN, FINISHED -> it.roleInGame
                }
                this.userClass = when (gameSession.state) {
                    NEW -> null
                    IN_PROGRESS, PENDING -> if (it.id == userId) {
                        it.classInGame
                    } else {
                        null
                    }

                    GAME_END_SCREEN, FINISHED -> it.classInGame
                }
            }
            this.gameSkin = userSkinDtoMaker.toDto(userSkins[it.userAccount.id]!!)
        }
    }

    private fun mapRolesByReceiverRoleAsAdmin(
        gameSession: GameSession,
        userSkins: Map<Long, UserSkinSettings>,
    ) = gameSession.usersOfGameSession.map {
        InGameUserDto().apply {
            this.userId = it.userAccount.id
            this.userName = it.userAccount.nickName
            this.isHost = it.host
            this.role = RoleDto().apply {
                this.userRole = when (gameSession.state) {
                    NEW -> null
                    IN_PROGRESS, PENDING -> it.roleInGame
                    GAME_END_SCREEN, FINISHED -> it.roleInGame
                }
                this.userClass = when (gameSession.state) {
                    NEW -> null
                    IN_PROGRESS, PENDING, GAME_END_SCREEN, FINISHED -> it.classInGame
                }
            }
            this.gameSkin = userSkinDtoMaker.toDto(userSkins[it.userAccount.id]!!)
        }
    }
}