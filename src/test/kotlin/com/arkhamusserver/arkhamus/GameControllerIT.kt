package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.utils.EnvironmentSetupUtil
import com.arkhamusserver.arkhamus.utils.FakeUserSetupUtil
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.INDIYSKIY
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.Q_CHAN
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.SITHOID
import com.arkhamusserver.arkhamus.utils.UserContainer.Companion.GRAF_D
import com.arkhamusserver.arkhamus.view.controller.GameController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals

@SpringBootTest
class GameControllerIT {

    @Autowired
    lateinit var environmentSetupUtil: EnvironmentSetupUtil

    @Autowired
    lateinit var gameController: GameController

    @Autowired
    lateinit var fakeUserSetupUtil: FakeUserSetupUtil

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
        assertEquals(1, createdGameSession.roleDtos!!.size)
        assertEquals(hostUser.id, createdGameSession.roleDtos!!.first().userId)
    }

    @Test
    fun `get CUSTOM game session`() {
        val hostUser = fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        fakeUserSetupUtil.fakeUser(GRAF_D)
        val gameById = gameController.getGame(gameSession.id!!).body!!

        assertEquals(GameState.NEW, gameById.state)
        assertEquals(1, gameById.roleDtos!!.size)
        assertEquals(hostUser.id, gameById.roleDtos!!.first().userId)

        val gameByPlayerId = gameController.findUsersOpenGame(hostUser.id!!).body!!

        assertEquals(GameState.NEW, gameByPlayerId.state)
        assertEquals(1, gameByPlayerId.roleDtos!!.size)
        assertEquals(hostUser.id, gameByPlayerId.roleDtos!!.first().userId)
    }

    @Test
    fun `join to CUSTOM game`() {
        val host = fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        val player2 = fakeUserSetupUtil.fakeUser(SITHOID)
        val gameByPlayer2 = gameController.connect(gameSession.id!!).body!!

        assertEquals(2, gameByPlayer2.roleDtos!!.size)
        assertEquals(1, gameByPlayer2.roleDtos!!.filter { it.userId != host.id }.size)
        assertEquals(player2.id, gameByPlayer2.roleDtos!!.first { it.userId != host.id }.userId)

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameById = gameController.getGame(gameSession.id!!).body!!
        assertEquals(2, gameById.roleDtos!!.size)
        assertEquals(1, gameById.roleDtos!!.filter { it.userId != host.id }.size)
        assertEquals(player2.id, gameById.roleDtos!!.first { it.userId != host.id }.userId)
    }

    @Test
    fun `start CUSTOM game`() {

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createCustom().body!!

        fakeUserSetupUtil.fakeUser(SITHOID)
        gameController.connect(gameSession.id!!)

        fakeUserSetupUtil.fakeUser(Q_CHAN)
        gameController.connect(gameSession.id!!)

        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        gameController.start(gameSession.id!!)

        val updatedGameSession = gameController.getGame(gameSession.id!!).body!!
        assertEquals(GameState.IN_PROGRESS, updatedGameSession.state)
    }

    @Test
    fun `start SINGLE game`() {
        fakeUserSetupUtil.fakeUser(INDIYSKIY)
        val gameSession = gameController.createSingle().body!!

        val started = gameController.start(gameSession.id!!).body!!
        assertEquals(GameState.IN_PROGRESS, started.state)
    }
}