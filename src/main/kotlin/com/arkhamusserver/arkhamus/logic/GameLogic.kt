package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
import com.arkhamusserver.arkhamus.logic.gamestart.GameStartLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionSettingsRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.*
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.GameState.NEW
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val gameSessionSettingsRepository: GameSessionSettingsRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameStartLogic: GameStartLogic,
    private val userSkinLogic: UserSkinLogic,
    private val currentUserService: CurrentUserService,
    private val gameValidator: GameValidator,
    private val gameSessionDtoMaker: GameSessionDtoMaker
) {

    companion object {
        const val RELATED_ENTITY = "Game"
        const val TOKEN_LENGTH = 8
    }

    @Transactional
    fun findCurrentUserGame(player: UserAccount, gameType: GameType): GameSession? {
        val userOfGames = userOfGameSessionRepository.findByUserAccountId(player.id!!)
        return userOfGames.firstOrNull {
            it.gameSession.state == NEW && it.gameSession.gameType == gameType && it.host
        }?.gameSession
    }

    @Transactional
    fun start(game: GameSession): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val invitedUsers = game.usersOfGameSession
        gameValidator.checkStartAccess(player, game, invitedUsers)
        val skins = userSkinLogic.allSkinsOf(game)
        val skinsReshuffled = userSkinLogic.reshuffleSkins(skins.values).associateBy { it.userAccount!!.id!! }
        startGame(game)
        val gameUpdated = gameSessionRepository.findById(game.id!!).get()
        return gameUpdated.toDto(player, skinsReshuffled)
    }

    fun disconnect() {
        val player = currentUserService.getCurrentUserAccount()
        disconnect(player)
    }

    fun disconnect(player: UserAccount) {
        val userGames =
            userOfGameSessionRepository.findByUserAccountId(player.id!!).filter { it.gameSession.state == NEW }

        userGames.forEach { userGame ->
            if (userGame.gameSession.gameType == GameType.SINGLE || userGame.gameSession.usersOfGameSession.size < 2) {
                userGame.gameSession.state = GameState.ABANDONED
            } else {
                if (userGame.host) {
                    trySetAnotherHost(userGame, player)
                }
                userGame.gameSession.usersOfGameSession = userGame.gameSession.usersOfGameSession.filter {
                    it.userAccount.id != player.id
                }
            }
        }
        gameSessionRepository.saveAll(userGames.map { it.gameSession })
    }

    fun findGameNullSafe(gameId: Long): GameSession = gameSessionRepository.findById(gameId).orElseThrow {
        ArkhamusServerRequestException(
            "game not found with id $gameId", RELATED_ENTITY
        )
    }

    fun findGameNullSafe(token: String): GameSession =
        gameSessionRepository.findByToken(token).firstOrNull() ?: throw ArkhamusServerRequestException(
            "game not found with token $token", RELATED_ENTITY
        )

    fun connectUserToGame(
        player: UserAccount, game: GameSession, host: Boolean = false
    ): UserOfGameSession {
        disconnect(player)
        val userOfGameSession = UserOfGameSession(
            userAccount = player,
            gameSession = game,
            host = host,
        )
        userOfGameSessionRepository.save(userOfGameSession)
        return userOfGameSession
    }

    fun createNewGameSession(
        lobbySize: Int, cultistSize: Int, gameType: GameType, player: UserAccount
    ): GameSession {
        disconnect()
        val gameSessionSettings = GameSessionSettings(
            numberOfCultists = cultistSize, lobbySize = lobbySize
        ).apply {
            this.level = null
        }.apply {
            gameSessionSettingsRepository.save(this)
        }
        return GameSession(
            state = NEW,
            token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH),
            gameType = gameType,
            gameSessionSettings = gameSessionSettings,
        ).apply {
            gameSessionRepository.save(this)
        }.also {
            val host = connectUserToGame(player, it, true)
            it.usersOfGameSession = listOf(host)
        }
    }

    private fun trySetAnotherHost(
        session: UserOfGameSession, account: UserAccount
    ) {
        session.gameSession.usersOfGameSession.firstOrNull {
            it.userAccount.id != account.id
        }?.let {
            it.host = true
        }
    }

    private fun GameSession.toDto(
        currentPlayer: UserAccount, skins: Map<Long, UserSkinSettings>
    ): GameSessionDto = gameSessionDtoMaker.toDto(
        this, skins, currentPlayer
    )

    private fun startGame(
        game: GameSession,
    ) {
        gameStartLogic.startGame(game)
    }
}