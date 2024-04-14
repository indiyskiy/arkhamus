package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.MockRedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.CloseContainerGameData
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
import org.junit.jupiter.api.Assertions.*
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
    fun emptyInventory() {
        val newInventoryContent = emptyList<ContainerCell>()

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertNull(resultUser[Item.I1.id])
        assertEquals(10, resultContainer[Item.I1.id])

        assertNull(resultUser[Item.I2.id])
        assertEquals(10, resultContainer[Item.I2.id])

        assertNull(resultUser[Item.I3.id])
        assertEquals(10, resultContainer[Item.I3.id])

        assertNull(resultUser[Item.I4.id])
        assertEquals(10, resultContainer[Item.I4.id])

        assertNull(resultUser[Item.I5.id])
        assertEquals(5, resultContainer[Item.I5.id])

        assertNull(resultUser[Item.I6.id])
        assertEquals(5, resultContainer[Item.I6.id])
    }

    @Test
    fun fullInventory() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.id, 10),
            ContainerCell(Item.I2.id, 10),
            ContainerCell(Item.I3.id, 10),
            ContainerCell(Item.I4.id, 10),
            ContainerCell(Item.I5.id, 5),
            ContainerCell(Item.I6.id, 5),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.id])
        assertNull(resultContainer[Item.I1.id])

        assertEquals(10, resultUser[Item.I2.id])
        assertNull(resultContainer[Item.I2.id])

        assertEquals(10, resultUser[Item.I3.id])
        assertNull(resultContainer[Item.I3.id])

        assertEquals(10, resultUser[Item.I4.id])
        assertNull(resultContainer[Item.I4.id])

        assertEquals(5, resultUser[Item.I5.id])
        assertNull(resultContainer[Item.I5.id])

        assertEquals(5, resultUser[Item.I6.id])
        assertNull(resultContainer[Item.I6.id])
    }

    @Test
    fun mixed() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.id, 10),
            ContainerCell(Item.I2.id, 0),
            ContainerCell(Item.I3.id, 3),
            ContainerCell(Item.I4.id, 10),
            ContainerCell(Item.I5.id, 3),
            ContainerCell(Item.I6.id, 3),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.id])
        assertNull(resultContainer[Item.I1.id])

        assertNull(resultUser[Item.I2.id])
        assertEquals(10, resultContainer[Item.I2.id])

        assertEquals(3, resultUser[Item.I3.id])
        assertEquals(7, resultContainer[Item.I3.id])

        assertEquals(10, resultUser[Item.I4.id])
        assertNull(resultContainer[Item.I4.id])

        assertEquals(3, resultUser[Item.I5.id])
        assertEquals(2, resultContainer[Item.I5.id])

        assertEquals(3, resultUser[Item.I6.id])
        assertEquals(2, resultContainer[Item.I6.id])
    }

    @Test
    fun tryToCheat() {
        val newInventoryContent = listOf(
            ContainerCell(Item.MASK.id, 3),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val closeContainerGameData = requestContainer.requestProcessData as CloseContainerGameData

        assertEquals(null, resultUser[Item.MASK.id])
        assertFalse(closeContainerGameData.sortedInventory!!.any { it.itemId == Item.MASK.id && it.number > 0 })
    }

    @Test
    fun sortedInventory() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.id, 10),
            ContainerCell(Item.I2.id, 0),
            ContainerCell(Item.I3.id, 0),
            ContainerCell(Item.I4.id, 10),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.id])
        assertNull(resultContainer[Item.I1.id])

        assertNull(resultUser[Item.I2.id])
        assertEquals(10, resultContainer[Item.I2.id])

        assertNull(resultUser[Item.I3.id])
        assertEquals(10, resultContainer[Item.I3.id])

        assertEquals(10, resultUser[Item.I4.id])
        assertNull(resultContainer[Item.I4.id])

        assertNull(resultUser[Item.I5.id])
        assertEquals(5, resultContainer[Item.I5.id])

        assertNull(resultUser[Item.I6.id])
        assertEquals(5, resultContainer[Item.I6.id])

        val closeContainerGameData = requestContainer.requestProcessData as CloseContainerGameData

        assertEquals(4, closeContainerGameData.sortedInventory!!.size)

        assertEquals(10, closeContainerGameData.sortedInventory!![0].number)
        assertEquals(Item.I1.id, closeContainerGameData.sortedInventory!![0].itemId)

        assertEquals(0, closeContainerGameData.sortedInventory!![1].number)
        assertEquals(Item.PURE_NOTHING.id, closeContainerGameData.sortedInventory!![1].itemId)

        assertEquals(0, closeContainerGameData.sortedInventory!![2].number)
        assertEquals(Item.PURE_NOTHING.id, closeContainerGameData.sortedInventory!![2].itemId)

        assertEquals(10, closeContainerGameData.sortedInventory!![3].number)
        assertEquals(Item.I4.id, closeContainerGameData.sortedInventory!![3].itemId)
    }

    private fun executeRequest(newInventoryContent: List<ContainerCell>): Pair<Data, NettyTickRequestMessageContainer> {
        val data = prepareDefaultData()

        val requestMessage = CloseContainerRequestMessage(
            containerId = data.redisContainer.containerId,
            newInventoryContent = newInventoryContent,
            type = "CloseContainerRequestMessage",
            baseRequestData = BaseRequestData(
                100L,
                UserPosition(
                    data.gameUser.x,
                    data.gameUser.y
                )
            )
        )

        val request = NettyTickRequestMessageContainer(
            nettyRequestMessage = requestMessage,
            channelId = "channel_id",
            userAccount = data.requestUserAccount,
            gameSession = data.gameSession,
            userRole = data.user,
            requestProcessData = data.oldContainer
        )

        closeContainerRequestProcessor.process(
            request,
            data.globalGameData,
            emptyList()
        )
        return data to request
    }

    private fun prepareDefaultData(): Data {
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
            classInGame = ClassInGame.MIND_HEALER,
            gameId = gameSession.id!!,
            x = container.x!!,
            y = container.y!!,
            madness = 20.0,
            madnessNotches = listOf(100.0, 300.0, 600.0),
            items = oldUserItems
        )

        val oldContainer = CloseContainerGameData(
            container = redisContainer,
            gameUser = gameUser,
            otherGameUsers = emptyList(),
            visibleOngoingEvents = emptyList(),
            tick = 100L
        )

        val globalGameData = GlobalGameData(
            game = redisGame,
            users = mapOf(gameUser.userId to gameUser),
            containers = mapOf(redisContainer.containerId to redisContainer),
            timeEvents = emptyList()
        )
        val data = Data(redisContainer, gameUser, requestUserAccount, gameSession, user, oldContainer, globalGameData)
        return data
    }

    private fun createOldUserItems(): MutableMap<Int, Long> {
        return mutableMapOf(
            Item.I1.id to 5,
            Item.I2.id to 5,
            Item.I3.id to 5,
            Item.I4.id to 5,
            Item.I5.id to 5,
        )
    }

    private fun createContainersItems(): MutableMap<Int, Long> {
        return mutableMapOf(
            Item.I1.id to 5,
            Item.I2.id to 5,
            Item.I3.id to 5,
            Item.I4.id to 5,
            Item.I6.id to 5,
        )
    }

    data class Data(
        val redisContainer: RedisContainer,
        val gameUser: RedisGameUser,
        val requestUserAccount: UserAccount,
        val gameSession: GameSession,
        val user: UserOfGameSession,
        val oldContainer: CloseContainerGameData,
        val globalGameData: GlobalGameData
    )
}