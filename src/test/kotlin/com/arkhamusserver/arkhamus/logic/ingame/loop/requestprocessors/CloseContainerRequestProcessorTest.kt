package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.MockRedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameData
import com.arkhamusserver.arkhamus.model.database.entity.*
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.model.enums.Role
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.CloseContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp

@SpringBootTest
class CloseContainerRequestProcessorTest {
    @Autowired
    private lateinit var redisDataAccess: MockRedisDataAccess

    @Autowired
    private lateinit var closeContainerRequestProcessor: CloseContainerRequestProcessor

    @BeforeEach
    fun setUp() {
        redisDataAccess.cleanUp()
    }

    @Test
    fun testThreadPoolSimple() {
        val requestUserAccount = UserAccount().apply {
            id = 1L
            nickName = "user"
            email = "email"
            password = "password"
            role = Role.USER
        }

        val level = Level(
            1,
            Timestamp(System.currentTimeMillis()),
            version = 1,
            levelId = 1,
            levelHeight = 1000,
            levelWidth = 1000,
            state = LevelState.ACTIVE
        )
        val gameSession = GameSession(
            id = 1L,
            creationTimestamp = Timestamp(System.currentTimeMillis()),
            usersOfGameSession = emptyList(),
            gameSessionSettings = GameSessionSettings(
                id = 1,
                lobbySize = 1,
                numberOfCultists = 0,
                level = level
            ),
            state = GameState.IN_PROGRESS,
            gameType = GameType.SINGLE,
            god = God.AAMON,
            token = "gametoken"
        )
        val redisGame = RedisGame(
            id = gameSession.id.toString(),
            gameId = gameSession.id,
            currentTick = 100L,
            globalTimer = 10000L,
            gameStart = System.currentTimeMillis(),
            state = GameState.PENDING.name
        )

        val user = UserOfGameSession(
            id = 1L,
            userAccount = requestUserAccount,
            gameSession = gameSession,
            host = true,
            roleInGame = RoleTypeInGame.INVESTIGATOR
        )

        gameSession.usersOfGameSession = listOf(user)

        val container = Container(
            id = 1L,
            inGameId = 1L,
            interactionRadius = 200.0,
            x = 50.0,
            y = 50.0
        )
        val inContainerItems = createContainersItems()

        val redisContainer = RedisContainer(
            id = "${gameSession.id}::${container.id}",
            containerId = container.id!!,
            gameId = gameSession.id!!,
            holdingUser = 1,
            state = MapObjectState.HOLD,
            x = container.x!!,
            y = container.y!!,
            interactionRadius = container.interactionRadius!!,
            items = inContainerItems
        )

        val oldUserItems = createOldUserItems()

        val gameUser = RedisGameUser(
            id = "${gameSession.id}::${user.id}",
            userId = user.id!!,
            nickName = "test user",
            role = RoleTypeInGame.INVESTIGATOR,
            gameId = gameSession.id!!,
            x = container.x!!,
            y = container.y!!,
            madness = 20.0,
            items = oldUserItems
        )

        val oldContainer = ContainerGameData(
            container = redisContainer,
            gameUser = gameUser,
            emptyList(),
            emptyList(),
            100L
        )

        val globalGameData = GlobalGameData(
            game = redisGame,
            users = mapOf(gameUser.userId to gameUser),
            containers = mapOf(redisContainer.containerId to redisContainer),
            timeEvents = emptyList()
        )

        val newInventoryContent = emptyList<ContainerCell>()

        val requestMessage = CloseContainerRequestMessage(
            containerId = redisContainer.containerId,
            newInventoryContent = newInventoryContent,
            type = "CloseContainerRequestMessage",
            baseRequestData = BaseRequestData(
                100L,
                UserPosition(
                    redisContainer.x - 1,
                    redisContainer.y - 1
                )
            )
        )

        val request = NettyTickRequestMessageContainer(
            nettyRequestMessage = requestMessage,
            channelId = "channel_id",
            userAccount = requestUserAccount,
            gameSession = gameSession,
            userRole = user,
            requestProcessData = oldContainer
        )

        closeContainerRequestProcessor.process(
            request,
            globalGameData,
            emptyList()
        )
        val resultUser = globalGameData.users[1L]!!.items
        val resultContainer = globalGameData.containers[1L]!!.items
        assertNull(resultUser[Item.I1.getId()])
        assertEquals(10, resultContainer[Item.I1.getId()])
    }

    private fun createOldUserItems(): MutableMap<Long, Long> {
        return mutableMapOf(
            Item.I1.getId() to 5,
            Item.I3.getId() to 5,
            Item.I4.getId() to 5,
            Item.I5.getId() to 5,
            Item.I7.getId() to 5,
        )
    }

    private fun createContainersItems(): MutableMap<Long, Long> {
        return mutableMapOf(
            Item.I1.getId() to 5,
            Item.I2.getId() to 5,
            Item.I3.getId() to 5,
            Item.I4.getId() to 5,
            Item.I6.getId() to 5,
        )
    }
}