package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.dataaccess.GameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameType
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameLogic(
    private val gameRepository: GameRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
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
}