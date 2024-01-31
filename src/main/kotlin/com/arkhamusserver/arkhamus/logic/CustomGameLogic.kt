package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionSettingsRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.service.LevelService
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState.NEW
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.maker.GameSessionSettingsDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component


@Component
class CustomGameLogic(
    private val gameLogic: GameLogic,
    private val gameSessionSettingsRepository: GameSessionSettingsRepository,
    private val levelService: LevelService,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val currentUserService: CurrentUserService,
    private val gameValidator: GameValidator,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val gameSessionSettingsDtoMaker: GameSessionSettingsDtoMaker,
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 8
        const val DEFAULT_CULTIST_SIZE = 2
    }

    @Transactional
    fun findGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.findGameNullSafe(gameId).toDto(player)
    }
    @Transactional
    fun findGame(token: String): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.findGameNullSafe(token).toDto(player)
    }

    @Transactional
    fun findUsersOpenGame(playerId: Long): GameSessionDto? {
        val player = currentUserService.getCurrentUserAccount()
        val hosted = userOfGameSessionRepository.findByUserAccountId(playerId).filter { it.host }
        hosted.sortedByDescending { it.gameCreationTimestamp }.forEach {
            val game = it.gameSession
            if (game.state == NEW) {
                return game.toDto(player)
            }
        }
        return null
    }

    @Transactional
    fun createGame(): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return gameLogic.createNewGameSession(
            DEFAULT_LOBBY_SIZE,
            DEFAULT_CULTIST_SIZE,
            GameType.CUSTOM,
            player
        ).toDto(player)
    }

    @Transactional
    fun connectToGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = gameLogic.findGameNullSafe(gameId)
//        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        gameValidator.checkJoinAccess(player, game)
        val connectedUser = gameLogic.connectUserToGame(player, game)
        val usersOfGameSession = game.usersOfGameSession?.let { it + connectedUser } ?: emptyList()
        game.usersOfGameSession = usersOfGameSession
        return game.toDto(player)
    }

    @Transactional
    fun updateLobby(gameId: Long, gameSessionSettingsDto: GameSessionSettingsDto): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = gameLogic.findGameNullSafe(gameId)
        val level = gameSessionSettingsDto.level?.let { levelDto ->
            levelDto.levelId?.let {
                levelService.latestByLevelIdAndVersion(it)
            }
        }
        gameValidator.checkUpdateAccess(player, game, gameSessionSettingsDto)
        updateSettings(game, level, gameSessionSettingsDto)
        return game.toDto(player)
    }

    private fun updateSettings(
        game: GameSession,
        level: Level?,
        gameSessionSettingsDto: GameSessionSettingsDto
    ) {
        val settings = game.gameSessionSettings
        gameSessionSettingsDtoMaker.merge(settings, level, gameSessionSettingsDto)
        val updatedSettings = gameSessionSettingsRepository.save(settings)
        game.gameSessionSettings = updatedSettings
    }

    private fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionDtoMaker.toDto(this, currentPlayer)
}


