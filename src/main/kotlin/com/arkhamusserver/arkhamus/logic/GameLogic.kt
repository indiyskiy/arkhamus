package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.logic.ingame.GameStartLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionToGameSessionDtoMaker
import com.arkhamusserver.arkhamus.view.validator.GameValidator
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameLogic(
    private val gameRepository: GameRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameStartLogic: GameStartLogic,
    private val currentUserService: CurrentUserService,
    private val gameValidator: GameValidator,
    private val gameSessionToGameSessionDtoMaker: GameSessionToGameSessionDtoMaker
    ) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun start(game: GameSession): GameSessionDto? {
        val player = currentUserService.getCurrentUserAccount()

        val invitedUsers = game.id?.let {
            userOfGameSessionRepository.findByGameSessionId(it)
        } ?: throw IllegalStateException("users of game session")

        gameValidator.checkStartAccess(player, game, invitedUsers)
        startGame(game)
        val cultistSize = when(game.gameType){
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
        val cultists = invitedUsers.shuffled(random).subList(0, game.numberOfCultists ?: cultistSize)
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

    fun startGame(game: GameSession) {
        game.god = God.values().random(random)
        game.state = GameState.IN_PROGRESS
        gameRepository.save(game)
        gameStartLogic.startGame(game)
    }

    fun findGameNullSafe(gameId: Long): GameSession = gameRepository.findById(gameId).orElseThrow {
        RuntimeException("wtf? $gameId")
    }

    fun connectUserToGame(
        player: UserAccount, game: GameSession, host: Boolean = false
    ): UserOfGameSession {
        val userOfGameSession = UserOfGameSession(
            userAccount = player,
            gameSession = game,
            host = host,
            gameCreationTimestamp = game.creationTimestamp
        )
        userOfGameSessionRepository.save(userOfGameSession)
        return userOfGameSession
    }

    fun createNewGameSession(
        lobbySize: Int,
        cultistSize: Int,
        gameType: GameType
    ) = GameSession(
        state = GameState.NEW,
        gameType = gameType,
        lobbySize = lobbySize,
        numberOfCultists = cultistSize
    ).apply {
        gameRepository.save(this)
    }

    fun GameSession.toDto(currentPlayer: UserAccount): GameSessionDto =
        gameSessionToGameSessionDtoMaker.toDto(this, currentPlayer)

}