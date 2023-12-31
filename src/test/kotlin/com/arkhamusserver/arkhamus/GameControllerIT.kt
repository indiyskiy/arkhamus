package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.utils.EnvironmentSetupUtil
import com.arkhamusserver.arkhamus.utils.FakeUserSetupUtil
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
    lateinit var userAccountRepository: UserAccountRepository

    @Autowired
    lateinit var userOfGameSessionRepository: UserOfGameSessionRepository

    @Autowired
    lateinit var fakeUserSetupUtil: FakeUserSetupUtil

    @BeforeEach
    fun setup() {
        environmentSetupUtil.setupEnvironment()
    }

    @Test
    fun `create game session`() {
        val users = userAccountRepository.findAll()
        val host = users.first()
        fakeUserSetupUtil.fakeUser(host)
        val gameSession = gameController.create().body!!

        fakeUserSetupUtil.fakeUser(host)
        val createdGameSession = gameController.getGame(gameSession.id!!).body!!

        assertEquals(createdGameSession.state, GameState.NEW)
        val usersOfGame = userOfGameSessionRepository.findByGameSessionId(createdGameSession.id!!)
        assertEquals(usersOfGame.size, 1)
        assertEquals(usersOfGame.first().userAccount.id, host.id)
        assertEquals(
            usersOfGame.first().gameSession.id,
            createdGameSession.id
        )
        assertEquals(usersOfGame.first().host, true)
    }

    @Test
    fun `join to the game`() {
        val users = userAccountRepository.findAll()
        val host = users.first()
        fakeUserSetupUtil.fakeUser(host)
        val gameSession = gameController.create().body!!

        val player2 = users.elementAt(1)!!
        fakeUserSetupUtil.fakeUser(player2)
        gameController.connect(gameSession.id!!)

        val usersOfGame = userOfGameSessionRepository.findByGameSessionId(gameSession.id!!)
        assertEquals(usersOfGame.size, 2)
        assertEquals(usersOfGame.filter { !it.host }.size, 1)
        assertEquals(usersOfGame.first { !it.host }.userAccount.id, player2.id)
        assertEquals(usersOfGame.first { !it.host }.gameSession.id, gameSession.id)
        assertEquals(usersOfGame.first { !it.host }.host, false)
    }

    @Test
    fun `start the game`() {
        val users = userAccountRepository.findAll()
        val host = users.first()
        fakeUserSetupUtil.fakeUser(host)
        val gameSession = gameController.create().body!!

        users.elementAt(1)!!.id!!
        gameController.connect(gameSession.id!!)
        gameController.start(gameSession.id!!)

        val updatedGameSession = gameController.getGame(gameSession.id!!).body!!
        assertEquals(updatedGameSession.state, GameState.IN_PROGRESS)
    }
}