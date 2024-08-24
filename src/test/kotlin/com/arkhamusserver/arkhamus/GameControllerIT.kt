package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.utils.EnvironmentSetupUtil
import com.arkhamusserver.arkhamus.utils.FakeUserSetupUtil
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.GRAF_D
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.INDIYSKIY
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.Q_CHAN
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.SITHOID
import com.arkhamusserver.arkhamus.view.controller.GameController
import com.arkhamusserver.arkhamus.view.controller.UserController
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GameControllerIT {

    @Autowired
    lateinit var environmentSetupUtil: EnvironmentSetupUtil

    @Autowired
    lateinit var gameController: GameController

    @Autowired
    lateinit var fakeUserSetupUtil: FakeUserSetupUtil

    @Autowired
    lateinit var userController: UserController

    @BeforeEach
    fun setup() {
        environmentSetupUtil.setupEnvironment()
    }

    @Test
    fun `create CUSTOM game session`() {
        val hostUser = fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        fakeUserSetupUtil.fakeUser(SITHOID)
        val createdGameSession = gameController.getGame(gameSession.id!!).body!!

        assertEquals(GameState.NEW, createdGameSession.state)
        assertEquals(1, createdGameSession.usersInGame!!.size)
        assertEquals(hostUser.id, createdGameSession.usersInGame!!.first().userId)
    }

    @Test
    fun `get CUSTOM game session`() {
        val hostUser = fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        fakeUserSetupUtil.fakeUser(GRAF_D)
        val gameById = gameController.getGame(gameSession.id!!).body!!

        assertEquals(GameState.NEW, gameById.state)
        assertEquals(1, gameById.usersInGame!!.size)
        assertEquals(hostUser.id, gameById.usersInGame!!.first().userId)

        val gameByPlayerId = gameController.findUsersOpenGame(hostUser.id!!).body!!

        assertEquals(GameState.NEW, gameByPlayerId.state)
        assertEquals(1, gameByPlayerId.usersInGame!!.size)
        assertEquals(hostUser.id, gameByPlayerId.usersInGame!!.first().userId)
    }

    @Test
    fun `join to CUSTOM game`() {
        val host = fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        val player2 = fakeUserSetupUtil.fakeUser(SITHOID)
        val gameByPlayer2 = gameController.connect(gameSession.id!!).body!!

        assertEquals(2, gameByPlayer2.usersInGame!!.size)
        assertEquals(1, gameByPlayer2.usersInGame!!.filter { it.userId != host.id }.size)
        assertEquals(player2.id, gameByPlayer2.usersInGame!!.first { it.userId != host.id }.userId)

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameById = gameController.getGame(gameSession.id!!).body!!
        assertEquals(2, gameById.usersInGame!!.size)
        assertEquals(1, gameById.usersInGame!!.filter { it.userId != host.id }.size)
        assertEquals(player2.id, gameById.usersInGame!!.first { it.userId != host.id }.userId)
    }

    @Test
    fun `start CUSTOM game`() {

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!
        val gameId = gameSession.id!!
        gameController.update(
            gameId,
            GameSessionSettingsDto(
                lobbySize = 3,
                numberOfCultists = 1,
                level = LevelDto(
                    levelId = 1,
                    version = 1
                )
            )
        ).body!!

        fakeUserSetupUtil.fakeUser(SITHOID)
        gameController.connect(gameSession.id!!)

        fakeUserSetupUtil.fakeUser(Q_CHAN)
        gameController.connect(gameSession.id!!)

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        gameController.start(gameSession.id!!)

        listOf(SITHOID, Q_CHAN, INDIYSKIY).forEach { nickName ->
            fakeUserSetupUtil.fakeUser(nickName)
            val user = userController.whoAmI()
            val updatedGameSession = gameController.getGame(gameSession.id!!).body!!
            assertEquals(GameState.PENDING, updatedGameSession.state)

            if (
                updatedGameSession.usersInGame!!.first {
                    it.nickName == user.nickName
                }.role!!.userRole == RoleTypeInGame.CULTIST
                ) {
                assertEquals(3, updatedGameSession.usersInGame!!.size)
                assertEquals(1, updatedGameSession.usersInGame!!.filter { it.isHost }.size)
                assertEquals(
                    1,
                    updatedGameSession.usersInGame!!.filter { it.role!!.userRole == RoleTypeInGame.CULTIST }.size
                )
                assertEquals(
                    2,
                    updatedGameSession.usersInGame!!.filter { it.role!!.userRole == RoleTypeInGame.INVESTIGATOR }.size
                )
            } else {
                assertEquals(3, updatedGameSession.usersInGame!!.size)
                assertEquals(1, updatedGameSession.usersInGame!!.filter { it.isHost }.size)
                assertEquals(
                    0,
                    updatedGameSession.usersInGame!!.filter { it.role!!.userRole == RoleTypeInGame.CULTIST }.size
                )
                assertEquals(
                    3,
                    updatedGameSession.usersInGame!!.filter { it.role!!.userRole == RoleTypeInGame.INVESTIGATOR }.size
                )
            }
        }

    }

    @Test
    fun `start SINGLE game`() {
        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createSingle().body!!
        val gameId = gameSession.id!!
        gameController.update(
            gameId,
            GameSessionSettingsDto(
                lobbySize = 1,
                numberOfCultists = 1,
                level = LevelDto(
                    levelId = 1,
                    version = 1
                )
            )
        ).body!!

        val started = gameController.start(gameSession.id!!).body!!
        assertEquals(GameState.PENDING, started.state)
        assertEquals(1, started.usersInGame!!.size)
        assertEquals(1, started.usersInGame!!.filter { it.isHost }.size)
        assertEquals(1, started.usersInGame!!.filter { it.role!!.userRole == RoleTypeInGame.CULTIST }.size)
    }
}