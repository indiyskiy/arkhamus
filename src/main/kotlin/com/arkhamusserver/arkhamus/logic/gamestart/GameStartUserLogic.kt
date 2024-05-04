package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.item.CultistClassByGodResolver
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.INVESTIGATOR
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartUserLogic(
    private val redisGameUserRepository: RedisGameUserRepository,
    private val startMarkerRepository: StartMarkerRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val cultistClassByGodResolver: CultistClassByGodResolver
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
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                userId = it.userAccount.id!!,
                nickName = it.userAccount.nickName!!,
                gameId = game.id!!,
                role = it.roleInGame!!,
                classInGame = it.classInGame!!,
                madness = 0.0,
                madnessNotches = listOf(100.0, 300.0, 600.0)
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
                it.roleInGame = CULTIST
                it.classInGame = cultistClassByGod(game.god!!)
            } else {
                it.roleInGame = INVESTIGATOR
                it.classInGame = randomInvestigatorRole()
            }
            userOfGameSessionRepository.save(it)
        }
    }

    private fun cultistClassByGod(god: God): ClassInGame =
        cultistClassByGodResolver.resolve(god)

    private fun randomInvestigatorRole(): ClassInGame =
        ClassInGame.values().filter { it.roleType == INVESTIGATOR }.random(random)

}