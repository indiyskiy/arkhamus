package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GameThreadPoolTest {

    @Autowired
    private lateinit var threadPool: GameThreadPool;

    @Autowired
    private lateinit var redisDataAccess: MockRedisDataAccess

    @Autowired
    private lateinit var responseSendingLoopManager: MockResponseSendingLoopManager

    @Autowired
    private lateinit var gameRelatedIdSource: GameRelatedIdSource

    private var gameSessionCounter = 0L
    private var userAccountCounter = 0L

    @BeforeEach
    fun setUp() {
        redisDataAccess.cleanUp()
    }

    // one game, only one player sends messages sequentially
    @Test
    fun testThreadPoolSimple() {
        val gameSession = setupGameSession(1)
        val userOfGameSession = gameSession.usersOfGameSession.first()

        threadPool.addTask(createMessage(tick = 0, gameSession = gameSession, userOfGameSession = userOfGameSession))

        Thread.sleep(1000)

        threadPool.addTask(createMessage(tick = 1, gameSession = gameSession, userOfGameSession = userOfGameSession))

        Thread.sleep(1000)

        var i = 0
        while (responseSendingLoopManager.collectedResponses.size < 2) {
            Thread.sleep(500)
            i++
            if (i == 20) break
        }

        // we generated two responses
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!
        assertEquals(2, collectedResponses.size)

        // and they are in proper order
        assertEquals(1, collectedResponses[0].tick)
        assertEquals(2, collectedResponses[1].tick)
    }

    // one game, only one player sends messages, but they arrive out of order
    fun testThreadPoolOutOfOrder() {
        TODO()
    }

    // one game, there are two players and messages they send arrive out of order
    fun testTwoPlayers() {
        TODO()
    }

    // two games, one player in both, both run smoothly
    fun testTwoGames() {
        TODO()
    }

    // two games, one freezes in processing for some reason
    fun testTwoGamesFreeze() {
        TODO()
    }

    // over GameThreadPool.MAX_POOL_SIZE games, at least MAX_POOL_SIZE games freeze for some reason
    fun testOverLimitGames() {
        TODO()
    }

    private fun createMessage(
        tick: Long,
        gameSession: GameSession,
        userOfGameSession: UserOfGameSession
    ): NettyTickRequestMessageContainer {
        return NettyTickRequestMessageContainer(
            HeartbeatRequestMessage(
                baseRequestData = BaseRequestData(
                    tick,
                    UserPosition(0.0, 0.0)
                ),
                type = "test-type"
            ),
            "my-channel",
            userAccount = userOfGameSession.userAccount,
            gameSession = gameSession,
            userRole = userOfGameSession
        )
    }

    private fun setupGameSession(usersCount: Int): GameSession {
        val gameSessionSettings = GameSessionSettings(
            id = gameSessionCounter++,
            lobbySize = usersCount,
            //TODO make sure we don't crash here later with validation
            numberOfCultists = usersCount,
            level = null
        )

        val gameSession = GameSession(
            id = gameSessionSettings.id,
            creationTimestamp = null,
            usersOfGameSession = emptyList(),
            gameSessionSettings = gameSessionSettings,
            // TODO meaningful state and game type once we start caring about it
            state = GameState.PENDING,
            gameType = GameType.DEFAULT
        )

        val usersOfGameSession = (0 until usersCount).map { _ ->
            val userAccount = UserAccount()
            userAccount.id = userAccountCounter
            userAccount.nickName = "user$userAccountCounter"
            userAccountCounter++
            UserOfGameSession(
                id = userAccount.id,
                userAccount = userAccount,
                host = false,
                roleInGame = RoleTypeInGame.CULTIST,
                gameSession = gameSession
            )
        }

        gameSession.usersOfGameSession = usersOfGameSession

        val redisGameUsers = usersOfGameSession.map { userOfGameSession ->
            RedisGameUser(
                id = gameRelatedIdSource.getId(gameSession.id!!, userOfGameSession.userAccount.id!!),
                userId = userOfGameSession.id!!,
                nickName = "user-nickname",
                role = RoleTypeInGame.INVESTIGATOR,
                gameId = gameSession.id!!,
                madness = 0.0
            )
        }

        val redisGame = RedisGame(
            gameSession.id.toString(),
            gameSession.id,
            currentTick = 0
        )

        val globalGameData = GlobalGameData(
            game = redisGame,
            users = redisGameUsers.associateBy { user -> user.userId }
        )

        redisDataAccess.setUp(listOf(globalGameData))

        return gameSession
    }
}