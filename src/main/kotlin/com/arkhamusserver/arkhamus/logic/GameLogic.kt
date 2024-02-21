package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
import com.arkhamusserver.arkhamus.logic.gamestart.GameStartLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionSettingsRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val gameSessionSettingsRepository: GameSessionSettingsRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameStartLogic: GameStartLogic,
    private val currentUserService: CurrentUserService,
    private val gameValidator: GameValidator,
    private val gameSessionDtoMaker: GameSessionDtoMaker
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
        const val RELATED_ENTITY = "Game"
        const val TOKEN_LENGTH = 8
    }

    fun start(game: GameSession): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val invitedUsers = game.usersOfGameSession
        gameValidator.checkStartAccess(player, game, invitedUsers)
        startGame(game)
        val cultistSize = when (game.gameType) {
            GameType.DEFAULT -> 0
            GameType.CUSTOM -> CustomGameLogic.DEFAULT_CULTIST_SIZE
            GameType.SINGLE -> SingleGameLogic.DEFAULT_CULTIST_SIZE
        }
        updateInvitedUsersInfoOnGameStart(game, invitedUsers, cultistSize)
        return game.toDto(player)
    }

    fun updateInvitedUsersInfoOnGameStart(
        game: GameSession,
        invitedUsers: List<UserOfGameSession>,
        cultistSize: Int
    ) {
        val cultists = invitedUsers
            .shuffled(random)
            .subList(
                0,
                game.gameSessionSettings.numberOfCultists
            )
        val cultistsIds = cultists.map { it.id }.toSet()
        invitedUsers.forEach {
            if (it.id in cultistsIds) {
                it.roleInGame = RoleTypeInGame.CULTIST
            } else {
                it.roleInGame = RoleTypeInGame.INVESTIGATOR
            }
            userOfGameSessionRepository.save(it)
        }
    }

    private fun startGame(game: GameSession) {
        game.god = God.values().random(random)
        game.state = GameState.PENDING
        gameSessionRepository.save(game)
        gameStartLogic.startGame(game)
    }

    fun findGameNullSafe(gameId: Long): GameSession = gameSessionRepository.findById(gameId).orElseThrow {
        ArkhamusServerRequestException(
            "game not found with id $gameId",
            RELATED_ENTITY
        )
    }

    fun findGameNullSafe(token: String): GameSession =
        gameSessionRepository.findByToken(token).firstOrNull() ?: throw ArkhamusServerRequestException(
            "game not found with token $token",
            RELATED_ENTITY
        )


    fun connectUserToGame(
        player: UserAccount, game: GameSession, host: Boolean = false
    ): UserOfGameSession {
        val userOfGameSession = UserOfGameSession(
            userAccount = player,
            gameSession = game,
            host = host,
        )
        userOfGameSessionRepository.save(userOfGameSession)
        return userOfGameSession
    }

    fun createNewGameSession(
        lobbySize: Int,
        cultistSize: Int,
        gameType: GameType,
        player: UserAccount
    ): GameSession {
        val gameSessionSettings = GameSessionSettings(
            numberOfCultists = cultistSize,
            lobbySize = lobbySize
        ).apply {
            this.level = null
        }.apply {
            gameSessionSettingsRepository.save(this)
        }
        return GameSession(
            state = GameState.NEW,
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

    fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionDtoMaker.toDto(this, currentPlayer)

}