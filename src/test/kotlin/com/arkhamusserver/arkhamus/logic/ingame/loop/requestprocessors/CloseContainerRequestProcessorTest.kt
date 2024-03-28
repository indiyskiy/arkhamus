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

        assertNull(resultUser[Item.I1.getId()])
        assertEquals(10, resultContainer[Item.I1.getId()])

        assertNull(resultUser[Item.I2.getId()])
        assertEquals(10, resultContainer[Item.I2.getId()])

        assertNull(resultUser[Item.I3.getId()])
        assertEquals(10, resultContainer[Item.I3.getId()])

        assertNull(resultUser[Item.I4.getId()])
        assertEquals(10, resultContainer[Item.I4.getId()])

        assertNull(resultUser[Item.I5.getId()])
        assertEquals(5, resultContainer[Item.I5.getId()])

        assertNull(resultUser[Item.I6.getId()])
        assertEquals(5, resultContainer[Item.I6.getId()])
    }

    @Test
    fun fullInventory() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.getId(), 10),
            ContainerCell(Item.I2.getId(), 10),
            ContainerCell(Item.I3.getId(), 10),
            ContainerCell(Item.I4.getId(), 10),
            ContainerCell(Item.I5.getId(), 5),
            ContainerCell(Item.I6.getId(), 5),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.getId()])
        assertNull(resultContainer[Item.I1.getId()])

        assertEquals(10, resultUser[Item.I2.getId()])
        assertNull(resultContainer[Item.I2.getId()])

        assertEquals(10, resultUser[Item.I3.getId()])
        assertNull(resultContainer[Item.I3.getId()])

        assertEquals(10, resultUser[Item.I4.getId()])
        assertNull(resultContainer[Item.I4.getId()])

        assertEquals(5, resultUser[Item.I5.getId()])
        assertNull(resultContainer[Item.I5.getId()])

        assertEquals(5, resultUser[Item.I6.getId()])
        assertNull(resultContainer[Item.I6.getId()])
    }

    @Test
    fun mixed() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.getId(), 10),
            ContainerCell(Item.I2.getId(), 0),
            ContainerCell(Item.I3.getId(), 3),
            ContainerCell(Item.I4.getId(), 10),
            ContainerCell(Item.I5.getId(), 3),
            ContainerCell(Item.I6.getId(), 3),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.getId()])
        assertNull(resultContainer[Item.I1.getId()])

        assertNull(resultUser[Item.I2.getId()])
        assertEquals(10, resultContainer[Item.I2.getId()])

        assertEquals(3, resultUser[Item.I3.getId()])
        assertEquals(7, resultContainer[Item.I3.getId()])

        assertEquals(10, resultUser[Item.I4.getId()])
        assertNull(resultContainer[Item.I4.getId()])

        assertEquals(3, resultUser[Item.I5.getId()])
        assertEquals(2, resultContainer[Item.I5.getId()])

        assertEquals(3, resultUser[Item.I6.getId()])
        assertEquals(2, resultContainer[Item.I6.getId()])
    }

    @Test
    fun tryToCheat() {
        val newInventoryContent = listOf(
            ContainerCell(Item.MASK.getId(), 3),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val closeContainerGameData = requestContainer.requestProcessData as CloseContainerGameData

        assertEquals(null, resultUser[Item.MASK.getId()])
        assertFalse(closeContainerGameData.sortedInventory!!.any { it.itemId == Item.MASK.getId() && it.number > 0 })
    }

    @Test
    fun sortedInventory() {
        val newInventoryContent = listOf(
            ContainerCell(Item.I1.getId(), 10),
            ContainerCell(Item.I2.getId(), 0),
            ContainerCell(Item.I3.getId(), 0),
            ContainerCell(Item.I4.getId(), 10),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.I1.getId()])
        assertNull(resultContainer[Item.I1.getId()])

        assertNull(resultUser[Item.I2.getId()])
        assertEquals(10, resultContainer[Item.I2.getId()])

        assertNull(resultUser[Item.I3.getId()])
        assertEquals(10, resultContainer[Item.I3.getId()])

        assertEquals(10, resultUser[Item.I4.getId()])
        assertNull(resultContainer[Item.I4.getId()])

        assertNull(resultUser[Item.I5.getId()])
        assertEquals(5, resultContainer[Item.I5.getId()])

        assertNull(resultUser[Item.I6.getId()])
        assertEquals(5, resultContainer[Item.I6.getId()])

        val closeContainerGameData = requestContainer.requestProcessData as CloseContainerGameData

        assertEquals(4, closeContainerGameData.sortedInventory!!.size)

        assertEquals(10, closeContainerGameData.sortedInventory!![0].number)
        assertEquals(Item.I1.getId(), closeContainerGameData.sortedInventory!![0].itemId)

        assertEquals(0, closeContainerGameData.sortedInventory!![1].number)
        assertEquals(Item.PURE_NOTHING.getId(), closeContainerGameData.sortedInventory!![1].itemId)

        assertEquals(0, closeContainerGameData.sortedInventory!![2].number)
        assertEquals(Item.PURE_NOTHING.getId(), closeContainerGameData.sortedInventory!![2].itemId)

        assertEquals(10, closeContainerGameData.sortedInventory!![3].number)
        assertEquals(Item.I4.getId(), closeContainerGameData.sortedInventory!![3].itemId)
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
            gameId = gameSession.id!!,
            x = container.x!!,
            y = container.y!!,
            madness = 20.0,
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

    private fun createOldUserItems(): MutableMap<Long, Long> {
        return mutableMapOf(
            Item.I1.getId() to 5,
            Item.I2.getId() to 5,
            Item.I3.getId() to 5,
            Item.I4.getId() to 5,
            Item.I5.getId() to 5,
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