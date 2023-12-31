package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.model.dataaccess.GameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleInGame
import com.arkhamusserver.arkhamus.view.maker.GameSessionToGameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import kotlin.random.Random


@Component
class GameLogic(
    private val gameRepository: GameRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val currentUserService: CurrentUserService,
    private val gameValidator: GameValidator,
    private val gameSessionToGameSessionDtoMaker: GameSessionToGameSessionDtoMaker
) {
    companion object {
        private const val DEFAULT_LOBBY_SIZE = 8
        private const val DEFAULT_CULTIST_SIZE = 1
        private val random: Random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun findGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return findGameNullSafe(gameId).toDto(player)
    }
    @Transactional
    fun findUsersOpenGame(playerId: Long): GameSessionDto? {
        val player = currentUserService.getCurrentUserAccount()
        val hosted = userOfGameSessionRepository.findByUserAccountId(playerId).filter { it.host }
        hosted.sortedByDescending { it.gameCreationTimestamp }.forEach {
            val game = it.gameSession
            if (game.state == GameState.NEW) {
                return game.toDto(player)
            }
        }
        return null
    }

    @Transactional
    fun createGame(): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        return createNewGameSession().also {
            connectUserToGame(player, it, true)
        }.toDto(player)
    }

    @Transactional
    fun connectToGame(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = findGameNullSafe(gameId)
        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        gameValidator.checkJoinAccess(player, game, invitedUsers)
        connectUserToGame(player, game)
        return game.toDto(player)
    }

    @Transactional
    fun start(gameId: Long): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = findGameNullSafe(gameId)
        val invitedUsers = userOfGameSessionRepository.findByGameSessionId(gameId)
        gameValidator.checkStartAccess(player, game, invitedUsers)
        startGame(game)
        updateInvitedUsersInfoOnGameStart(game, invitedUsers)
        return game.toDto(player)
    }


    @Transactional
    fun updateLobby(gameId: Long, gameSessionDto: GameSessionDto): GameSessionDto {
        val player = currentUserService.getCurrentUserAccount()
        val game = findGameNullSafe(gameId)
        gameValidator.checkUpdateAccess(player, game, gameSessionDto)
        gameSessionToGameSessionDtoMaker.merge(game, gameSessionDto)
        gameRepository.save(game)
        return game.toDto(player)
    }

    private fun updateInvitedUsersInfoOnGameStart(
        game: GameSession,
        invitedUsers: List<UserOfGameSession>
    ) {
        val cultists = invitedUsers.shuffled(random).subList(0, game.numberOfCultists ?: DEFAULT_CULTIST_SIZE)
        val cultistsIds = cultists.map { it.id }.toSet()
        invitedUsers.forEach {
            if (it.id in cultistsIds) {
                it.roleInGame = RoleInGame.CULTIST
            } else {
                it.roleInGame = RoleInGame.INVESTIGATORS
            }
            userOfGameSessionRepository.save(it)
        }
    }

    private fun startGame(game: GameSession) {
        game.god = God.values().random(random)
        game.state = GameState.IN_PROGRESS
        gameRepository.save(game)
    }

    private fun findGameNullSafe(gameId: Long): GameSession =
        gameRepository.findById(gameId).orElseThrow {
            RuntimeException("wtf?")
        }

    private fun connectUserToGame(
        player: UserAccount,
        game: GameSession,
        host: Boolean = false
    ) {
        val userOfGameSession = UserOfGameSession(
            userAccount = player,
            gameSession = game,
            host = host,
            gameCreationTimestamp = game.creationTimestamp
        )
        userOfGameSessionRepository.save(userOfGameSession)
    }

    private fun createNewGameSession() =
        GameSession(
            state = GameState.NEW,
            lobbySize = DEFAULT_LOBBY_SIZE,
            numberOfCultists = DEFAULT_CULTIST_SIZE
        ).apply {
            gameRepository.save(this)
        }

    private fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionToGameSessionDtoMaker.toDto(this, currentPlayer)

}


