package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionSettingsRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.service.LevelService
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.GameState.NEW
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.maker.GameSessionSettingsDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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
    private val userSkinLogic: UserSkinLogic,
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
        val hosted = userOfGameSessionRepository.findByUserAccountIdAndLeft(playerId).filter { it.host }
        hosted.sortedByDescending { it.gameSession.creationTimestamp }.forEach {
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
        return connect(game, player)
    }

    @Transactional
    fun connectToGameByToken(token: String): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = gameLogic.findGameNullSafe(token)
        return connect(game, player)
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

    private fun connect(
        game: GameSession,
        player: UserAccount
    ): GameSessionDto {
        fixColors(game, player)
        return joinToGame(game, player)
    }

    private fun joinToGame(
        game: GameSession,
        player: UserAccount
    ): GameSessionDto {
        gameValidator.checkJoinAccess(player, game)
        val connectedUser = gameLogic.connectUserToGame(player, game)
        if ((connectedUser.id) !in game.usersOfGameSession.map { it.id }) {
            val usersOfGameSession = game.usersOfGameSession + connectedUser
            game.usersOfGameSession = usersOfGameSession
        } else {
            game.usersOfGameSession.first { it.id == connectedUser.id }.left = false
        }
        return game.toDto(player)
    }

    private fun fixColors(
        session: GameSession,
        account: UserAccount
    ) {
        userSkinLogic.fixColors(
            session,
            account
        )
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
        gameSessionDtoMaker.toDto(this, userSkinLogic.allSkinsOf(this), currentPlayer)

}


