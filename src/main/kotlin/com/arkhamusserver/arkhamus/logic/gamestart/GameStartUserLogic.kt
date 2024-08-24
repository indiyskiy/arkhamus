package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.INVESTIGATOR
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartUserLogic(
    private val redisGameUserRepository: RedisGameUserRepository,
    private val startMarkerRepository: StartMarkerRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val redisGameRepository: RedisGameRepository
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
        private val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
    }

    @Transactional
    fun leaveFromPreviousGames(game: GameSession) {
        game.usersOfGameSession.forEach { userOfGameSession ->
            val usersInGames = redisGameUserRepository.findByUserId(userOfGameSession.userAccount.id!!)
            usersInGames.forEach { userInGame ->
                if (userInGame.gameId != game.id) {
                    val redisGame = redisGameRepository.findByGameId(userInGame.gameId)
                    if (redisGame.state in setOf(
                            GameState.NEW.name,
                            GameState.PENDING.name,
                            GameState.IN_PROGRESS.name,
                        )
                    ) {
                        logger.info("user ${userInGame.userId} started another game so he disconnected from ${userInGame.gameId}")
                        userInGame.leftTheGame = true
                        redisGameUserRepository.save(userInGame)
                    }
                }
            }
        }
    }

    fun createGameUsers(levelId: Long, game: GameSession): List<RedisGameUser> {
        updateInvitedUsersInfoOnGameStart(game)
        return createRedisUsers(levelId, game)
    }

    private fun createRedisUsers(levelId: Long, game: GameSession): List<RedisGameUser> {
        val startMarkers = startMarkerRepository.findByLevelId(levelId)
        val redisGameUsers = game.usersOfGameSession.map {
            val marker = startMarkers.random(GameStartLogic.random)
            val redisGameUser = RedisGameUser(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                userId = it.userAccount.id!!,
                nickName = it.userAccount.nickName,
                gameId = game.id!!,
                role = it.roleInGame!!,
                classInGame = it.classInGame!!,
                madness = 0.0,
                madnessNotches = listOf(100.0, 300.0, 600.0),
                connected = true
            ).apply {
                this.x = marker.point.x
                this.y = marker.point.y
            }
            GameStartLogic.logger.info("user placed to $redisGameUser")
            redisGameUser
        }
        redisGameUserRepository.saveAll(redisGameUsers)
        return redisGameUsers
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
                it.roleInGame = CULTIST
                it.classInGame = randomCultistClass()
            } else {
                it.roleInGame = INVESTIGATOR
                it.classInGame = randomInvestigatorRole()
            }
            userOfGameSessionRepository.save(it)
        }
    }

    private fun randomCultistClass(): ClassInGame =
        ClassInGame.values().filter { it.roleType == CULTIST }.random(random)

    private fun randomInvestigatorRole(): ClassInGame =
        ClassInGame.values().filter { it.roleType == INVESTIGATOR }.random(random)

}