package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ExecutedAction
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.HeartbeatRequestGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import com.fasterxml.uuid.Generators
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GameThreadPoolTest {

    companion object {
        private var gameSessionCounter = 0L
        private var userAccountCounter = 0L
    }

    @Autowired
    private lateinit var threadPool: GameThreadPool

    @Autowired
    private lateinit var redisDataAccess: MockRedisDataAccess

    @Autowired
    private lateinit var responseSendingLoopManager: MockResponseSendingLoopManager

    @BeforeEach
    fun setUp() {
        redisDataAccess.cleanUp()
        responseSendingLoopManager.cleanUp()
    }

    // one game, only one player sends messages sequentially
    @Test
    fun testThreadPoolSimple() {
        val (gameSession, globalGameData) = setupGameSession(1)
        val userOfGameSession = gameSession.usersOfGameSession.first()

        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = userOfGameSession,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession,
                userOfGameSession = userOfGameSession,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        waitUntilMessagesCount(2)

        // we generated two responses
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!
        assertEquals(2, collectedResponses.size)

        // and they are in proper order
        assertEquals(1, collectedResponses[0].tick)
        assertEquals(2, collectedResponses[1].tick)
    }

    // one game, only one player sends messages, but they arrive out of order
    @Test
    fun testThreadPoolOutOfOrder() {
        val (gameSession, globalGameData) = setupGameSession(usersCount = 1, startingTick = 1)
        val userOfGameSession = gameSession.usersOfGameSession.first()

        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession,
                userOfGameSession = userOfGameSession,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = userOfGameSession,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        waitUntilMessagesCount(1)

        // we generated one response
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!
        assertEquals(1, collectedResponses.size)

        // and only response for the latter tick
        assertEquals(2, collectedResponses[0].tick)
    }

    // one game, there are two players and messages they send arrive out of order
    @Test
    fun testTwoPlayers() {
        val (gameSession, globalGameData) = setupGameSession(usersCount = 2, startingTick = 0)
        val user1 = gameSession.usersOfGameSession[0]
        val user2 = gameSession.usersOfGameSession[1]

        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = user1,
                globalGameData = globalGameData
            )
        )
        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = user2,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession,
                userOfGameSession = user1,
                globalGameData = globalGameData
            )
        )
        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession,
                userOfGameSession = user2,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        waitUntilMessagesCount(4)

        // we generated 4 responses
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!
        assertEquals(4, collectedResponses.size)

        // and they are in proper order
        assertEquals(1, collectedResponses[0].tick)
        assertEquals(1, collectedResponses[1].tick)
        assertEquals(2, collectedResponses[2].tick)
        assertEquals(2, collectedResponses[3].tick)
    }

    // two games, one player in both, both run smoothly
    @Test
    fun testTwoGames() {
        val (gameSession1, globalGameData1) = setupGameSession(1)
        val (gameSession2, globalGameData2) = setupGameSession(1)
        val user1 = gameSession1.usersOfGameSession.first()
        val user2 = gameSession2.usersOfGameSession.first()

        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession1,
                userOfGameSession = user1,
                globalGameData = globalGameData1
            )
        )
        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession2,
                userOfGameSession = user2,
                globalGameData = globalGameData2
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession1,
                userOfGameSession = user1,
                globalGameData = globalGameData1
            )
        )
        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession2,
                userOfGameSession = user2,
                globalGameData = globalGameData2
            )
        )

        Thread.sleep(500)

        waitUntilMessagesCount(4)

        // we generated 2 responses for every game
        val collectedResponses1 = responseSendingLoopManager.collectedResponses[gameSession1.id]!!
        assertEquals(2, collectedResponses1.size)

        val collectedResponses2 = responseSendingLoopManager.collectedResponses[gameSession2.id]!!
        assertEquals(2, collectedResponses2.size)

        // and they are in order in every game
        assertEquals(1, collectedResponses1[0].tick)
        assertEquals(2, collectedResponses1[1].tick)
        assertEquals(1, collectedResponses2[0].tick)
        assertEquals(2, collectedResponses2[1].tick)
    }

    // one game, two players, one is stuck, we would like the processing to continue
    @Test
    fun testTwoPlayersFreeze() {
        val (gameSession, globalGameData) = setupGameSession(usersCount = 2, startingTick = 0)
        val user1 = gameSession.usersOfGameSession[0]
        val user2 = gameSession.usersOfGameSession[1]

        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = user1,
                globalGameData = globalGameData
            )
        )
        threadPool.addTask(
            createMessage(
                tick = 0,
                gameSession = gameSession,
                userOfGameSession = user2,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 1,
                gameSession = gameSession,
                userOfGameSession = user1,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        threadPool.addTask(
            createMessage(
                tick = 2,
                gameSession = gameSession,
                userOfGameSession = user1,
                globalGameData = globalGameData
            )
        )

        Thread.sleep(500)

        waitUntilMessagesCount(4)

        // we generated 4 responses
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!
        assertEquals(4, collectedResponses.size)

        // and they are in proper order
        assertEquals(1, collectedResponses[0].tick)
        assertEquals(1, collectedResponses[1].tick)
        assertEquals(2, collectedResponses[2].tick)
        assertEquals(3, collectedResponses[3].tick)
    }

    // over GameThreadPool.MAX_POOL_SIZE games, at least MAX_POOL_SIZE games freeze for some reason
    fun testOverLimitGames() {
        TODO()
    }

    // we'll have a ticker entity that actually handles processing of ticks in time, and timed one will be used for prod, manually triggered one will be used for tests
    private fun ensureClientState(
        gameSession: GameSession,
        tick: Long,
        clientUserId: Long,
        expectedState: List<UserPositionData>
    ) {
        val collectedResponses = responseSendingLoopManager.collectedResponses[gameSession.id]!!

    }

    // subset of NettyGameUserResponseMessage to ensure we don't tangle test data with implementation
    private data class UserPositionData(
        val id: Long,
        val x: Double,
        val y: Double
    )

    private fun waitUntilMessagesCount(count: Int) {
        waitForProcessing { responseSendingLoopManager.collectedResponses.size >= count }
    }

    private fun waitForProcessing(stopPredicate: () -> Boolean) {
        var i = 0
        while (!stopPredicate()) {
            Thread.sleep(500)
            i++
            if (i == 20) break
        }
    }

    private fun createMessage(
        tick: Long,
        gameSession: GameSession,
        userOfGameSession: UserOfGameSession,
        globalGameData: GlobalGameData
    ): NettyTickRequestMessageDataHolder {
        val redisGameUser = globalGameData.users[userOfGameSession.id]!!
        val otherGameUsers = globalGameData.users.values.filter { it.userId != userOfGameSession.id }
        return NettyTickRequestMessageDataHolder(
            HeartbeatRequestMessage(
                baseRequestData = BaseRequestData(
                    tick,
                    UserPosition(0.0, 0.0, 0.0)
                ),
                type = "test-type"
            ),
            "my-channel",
            userAccount = userOfGameSession.userAccount,
            gameSession = gameSession,
            userRole = userOfGameSession,
            requestProcessData = HeartbeatRequestGameData(
                gameUser = redisGameUser,
                otherGameUsers = otherGameUsers,
                visibleOngoingEvents = emptyList(),
                availableAbilities = emptyList(),
                ongoingCraftingProcess = emptyList(),
                visibleItems = emptyList(),
                containers = emptyList(),
                crafters = emptyList(),
                inZones = emptyList(),
                clues = emptyList(),
                userQuestProgresses = emptyList(),
                tick = tick + 1
            ),
            lastExecutedAction = ExecutedAction(-1, true, "")
        )
    }

    private fun setupGameSession(usersCount: Int, startingTick: Long = 0): Pair<GameSession, GlobalGameData> {
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
            val userAccount = UserAccount(nickName = "test")
            userAccount.id = userAccountCounter
            userAccount.nickName = "user$userAccountCounter"
            userAccountCounter++
            UserOfGameSession(
                id = userAccount.id,
                userAccount = userAccount,
                host = false,
                roleInGame = RoleTypeInGame.CULTIST,
                classInGame = ClassInGame.ARISTOCRAT,
                gameSession = gameSession
            )
        }

        gameSession.usersOfGameSession = usersOfGameSession

        val redisGameUsers = usersOfGameSession.map { userOfGameSession ->
            RedisGameUser(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                userId = userOfGameSession.id!!,
                nickName = "user-nickname",
                role = RoleTypeInGame.INVESTIGATOR,
                classInGame = ClassInGame.MIND_HEALER,
                gameId = gameSession.id!!,
                madness = 0.0,
                madnessNotches = listOf(100.0, 300.0, 600.0),
                connected = true,
                x = 0.0,
                y = 0.0,
                z = 0.0,
                items = mutableMapOf(),
                stateTags = mutableSetOf(),
                madnessDebuffs = mutableSetOf(),
                callToArms = 0,
                won = null,
                sawTheEndOfTimes = false,
                leftTheGame = false,
                visibilityModifiers = mutableSetOf()
            )
        }

        val redisGame = RedisGame(
            gameSession.id.toString(),
            gameSession.id,
            1,
            currentTick = startingTick
        )

        val globalGameData = GlobalGameData(
            game = redisGame,
            altarHolder = RedisAltarHolder(
                id = "altar holder",
                gameId = redisGame.gameId!!,
                altarHolderId = 0L,
                x = 0.0,
                y = 0.0,
                z = 0.0,
                radius =0.0,
                lockedGodId = null,
                itemsForRitual = mutableMapOf(),
                itemsIdToAltarId = mutableMapOf(),
                itemsOnAltars = mutableMapOf(),
                state = MapAltarState.OPEN,
            ),
            users = redisGameUsers.associateBy { user -> user.userId },
        )

        redisDataAccess.setUp(listOf(globalGameData))

        threadPool.initTickProcessingLoop(gameSession)

        return gameSession to globalGameData
    }
}