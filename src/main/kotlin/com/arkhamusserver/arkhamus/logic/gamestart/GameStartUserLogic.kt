package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartUserLogic(
    private val inGameGameUserRepository: InGameGameUserRepository,
    private val startMarkerRepository: StartMarkerRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val inRamGameRepository: InRamGameRepository
) {

    companion object {
        private val random: Random = Random(System.currentTimeMillis())
        private val logger: Logger = LoggerFactory.getLogger(GameStartLogic::class.java)
    }

    @Transactional
    fun leaveFromPreviousGames(game: GameSession) {
        game.usersOfGameSession.forEach { userOfGameSession ->
            val usersInGames = inGameGameUserRepository.findByUserId(userOfGameSession.userAccount.id!!)
            usersInGames.forEach { userInGame ->
                if (userInGame.gameId != game.id) {
                    val inRamGame = inRamGameRepository.findByGameId(userInGame.gameId).first()
                    if (inRamGame.state in setOf(
                            GameState.NEW.name,
                            GameState.PENDING.name,
                            GameState.IN_PROGRESS.name,
                        )
                    ) {
                        logger.info("user ${userInGame.inGameId()} started another game so he disconnected from ${userInGame.gameId}")
                        userInGame.leftTheGame = true
                        inGameGameUserRepository.save(userInGame)
                    }
                }
            }
        }
    }

    fun createGameUsers(
        levelId: Long,
        game: GameSession,
        skins: Map<Long, UserSkinSettings>
    ): List<InGameUser> {
        updateInvitedUsersInfoOnGameStart(game)
        return createInGameUsers(levelId, game, skins)
    }

    private fun createInGameUsers(
        levelId: Long,
        game: GameSession,
        skins: Map<Long, UserSkinSettings>
    ): List<InGameUser> {
        val startMarkers = startMarkerRepository.findByLevelId(levelId)
        val inGameUsers = game.usersOfGameSession.map {
            val marker = startMarkers.random(GameStartLogic.random)
            val inGameUser = InGameUser(
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
                originalSkin = InGameUserSkinSetting(
                    skinColor = skins[it.userAccount.id]!!.skinColor
                ),
            )
            GameStartLogic.logger.info("user placed to $inGameUser")
            inGameUser
        }
        inGameGameUserRepository.saveAll(inGameUsers)
        return inGameUsers
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
            ClassInGame.values().filter { it.globalTurnedOn && it.roleType == CULTIST }.random(random)
        } else {
            classesInGame.filter { it.globalTurnedOn && it.roleType == CULTIST }.random(random)
        }


    private fun randomInvestigatorRole(classesInGame: Set<ClassInGame>): ClassInGame =
        if (classesInGame.none { it.roleType == INVESTIGATOR }) {
            ClassInGame.values().filter { it.globalTurnedOn && it.roleType == INVESTIGATOR }.random(random)
        } else {
            classesInGame.filter { it.globalTurnedOn && it.roleType == INVESTIGATOR }.random(random)
        }
}