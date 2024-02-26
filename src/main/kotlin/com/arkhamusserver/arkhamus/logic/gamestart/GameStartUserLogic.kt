package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartUserLogic(
    private val redisGameUserRepository: RedisGameUserRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val startMarkerRepository: StartMarkerRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
    }

    fun createGameUsers(levelId: Long, game: GameSession) {
        updateInvitedUsersInfoOnGameStart(game)
        createRedisUsers(levelId, game)
    }

    private fun createRedisUsers(levelId: Long, game: GameSession) {
        val startMarkers = startMarkerRepository.findByLevelId(levelId)
        game.usersOfGameSession.forEach {
            val marker = startMarkers.random(GameStartLogic.random)
            val redisGameUser = RedisGameUser(
                id = gameRelatedIdSource.getId(game.id!!, it.userAccount.id!!),
                userId = it.userAccount.id!!,
                nickName = it.userAccount.nickName!!,
                gameId = game.id!!,
                role = it.roleInGame!!,
                mind = 600
            ).apply {
                this.x = marker.x!!
                this.y = marker.y!!
            }
            redisGameUserRepository.save(redisGameUser)
            GameStartLogic.logger.info("user placed to $redisGameUser")
        }
    }

    fun updateInvitedUsersInfoOnGameStart(
        game: GameSession
    ) {
        val cultists = game.usersOfGameSession
            .shuffled(random)
            .subList(
                0,
                game.gameSessionSettings.numberOfCultists
            )
        val cultistsIds = cultists.map { it.id }.toSet()
        game.usersOfGameSession.forEach {
            if (it.id in cultistsIds) {
                it.roleInGame = RoleTypeInGame.CULTIST
            } else {
                it.roleInGame = RoleTypeInGame.INVESTIGATOR
            }
            userOfGameSessionRepository.save(it)
        }
    }
}