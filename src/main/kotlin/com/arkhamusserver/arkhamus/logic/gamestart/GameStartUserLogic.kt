package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.parts.RedisUserSkinSetting
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
                        logger.info("user ${userInGame.inGameId()} started another game so he disconnected from ${userInGame.gameId}")
                        userInGame.leftTheGame = true
                        redisGameUserRepository.save(userInGame)
                    }
                }
            }
        }
    }

    fun createGameUsers(
        levelId: Long,
        game: GameSession,
        skins: Map<Long, UserSkinSettings>
    ): List<RedisGameUser> {
        updateInvitedUsersInfoOnGameStart(game)
        return createRedisUsers(levelId, game, skins)
    }

    private fun createRedisUsers(
        levelId: Long,
        game: GameSession,
        skins: Map<Long, UserSkinSettings>
    ): List<RedisGameUser> {
        val startMarkers = startMarkerRepository.findByLevelId(levelId)
        val redisGameUsers = game.usersOfGameSession.map {
            val marker = startMarkers.random(GameStartLogic.random)
            val redisGameUser = RedisGameUser(
                id = generateRandomId(),
                userId = it.userAccount.id!!,
                nickName = it.userAccount.nickName,
                gameId = game.id!!,
                role = it.roleInGame!!,
                classInGame = it.classInGame!!,
                madness = 0.0,
                madnessNotches = listOf(
                    GlobalGameSettings.MAX_USER_MADNESS / 6.0,
                    GlobalGameSettings.MAX_USER_MADNESS / 2.0,
                    GlobalGameSettings.MAX_USER_MADNESS
                ),
                x = marker.x,
                y = marker.y,
                z = marker.z,
                callToArms = game.gameSessionSettings.maxCallToArms,
                connected = true,
                visibilityModifiers = visibleModifiersByRole(it.roleInGame!!),
                originalSkin = RedisUserSkinSetting(
                    skinColor = skins[it.userAccount.id]!!.skinColor
                ),
            )
            GameStartLogic.logger.info("user placed to $redisGameUser")
            redisGameUser
        }
        redisGameUserRepository.saveAll(redisGameUsers)
        return redisGameUsers
    }

    private fun visibleModifiersByRole(game: RoleTypeInGame): Set<VisibilityModifier> =
        when (game) {
            CULTIST -> listOf(VisibilityModifier.ALL, VisibilityModifier.CULTIST)
            INVESTIGATOR -> listOf(VisibilityModifier.ALL, VisibilityModifier.INVESTIGATOR)
            NEUTRAL -> listOf(VisibilityModifier.ALL, VisibilityModifier.NEUTRAL)
        }.toSet()


    fun updateInvitedUsersInfoOnGameStart(
        game: GameSession
    ) {
        val notLeft = game.usersOfGameSession.filter { !it.leftTheLobby }
        val cultists = notLeft
            .shuffled(random)
            .subList(
                0,
                game.gameSessionSettings.numberOfCultists
            )
        val cultistsIds = cultists.map { it.userAccount.id }.toSet()
        notLeft.forEach {
            if (it.userAccount.id in cultistsIds) {
                it.roleInGame = CULTIST
                it.classInGame = randomCultistClass(game.gameSessionSettings.classesInGame)
            } else {
                it.roleInGame = INVESTIGATOR
                it.classInGame = randomInvestigatorRole(game.gameSessionSettings.classesInGame)
            }
            userOfGameSessionRepository.save(it)
        }
    }

    private fun randomCultistClass(classesInGame: Set<ClassInGame>): ClassInGame =
        if (classesInGame.none { it.roleType == CULTIST }) {
            ClassInGame.values().filter { it.turnedOn && it.roleType == CULTIST }.random(random)
        } else {
            classesInGame.filter { it.turnedOn && it.roleType == CULTIST }.random(random)
        }


    private fun randomInvestigatorRole(classesInGame: Set<ClassInGame>): ClassInGame =
        if (classesInGame.none { it.roleType == INVESTIGATOR }) {
            ClassInGame.values().filter { it.turnedOn && it.roleType == INVESTIGATOR }.random(random)
        } else {
            classesInGame.filter { it.turnedOn && it.roleType == INVESTIGATOR }.random(random)
        }
}