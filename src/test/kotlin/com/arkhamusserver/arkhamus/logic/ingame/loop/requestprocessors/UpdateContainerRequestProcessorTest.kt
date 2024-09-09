package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.MockRedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ExecutedAction
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.UpdateContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container.UpdateContainerRequestProcessor
import com.arkhamusserver.arkhamus.model.database.entity.*
import com.arkhamusserver.arkhamus.model.database.entity.game.Container
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.model.enums.RoleName
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container.UpdateContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.fasterxml.uuid.Generators
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp

@SpringBootTest
class UpdateContainerRequestProcessorTest {
    @Autowired
    private lateinit var redisDataAccess: MockRedisDataAccess

    @Autowired
    private lateinit var updateContainerRequestProcessor: UpdateContainerRequestProcessor

    @BeforeEach
    fun setUp() {
        redisDataAccess.cleanUp()
    }

    @Test
    fun emptyInventory() {
        val newInventoryContent = emptyList<InventoryCell>()

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertNull(resultUser[Item.SAINT_QUARTZ.id])
        assertEquals(10, resultContainer[Item.SAINT_QUARTZ.id])

        assertNull(resultUser[Item.CRYSTALLIZED_BLOOD.id])
        assertEquals(10, resultContainer[Item.CRYSTALLIZED_BLOOD.id])

        assertNull(resultUser[Item.ELDER_SIGN.id])
        assertEquals(10, resultContainer[Item.ELDER_SIGN.id])

        assertNull(resultUser[Item.CORRUPTED_TOPAZ.id])
        assertEquals(10, resultContainer[Item.CORRUPTED_TOPAZ.id])

        assertNull(resultUser[Item.HIGGS_BOSON.id])
        assertEquals(5, resultContainer[Item.HIGGS_BOSON.id])

        assertNull(resultUser[Item.INNSMOUTH_WATER.id])
        assertEquals(5, resultContainer[Item.INNSMOUTH_WATER.id])
    }

    @Test
    fun fullInventory() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD.id, 10),
            InventoryCell(Item.ELDER_SIGN.id, 10),
            InventoryCell(Item.CORRUPTED_TOPAZ.id, 10),
            InventoryCell(Item.HIGGS_BOSON.id, 5),
            InventoryCell(Item.INNSMOUTH_WATER.id, 5),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertNull(resultContainer[Item.SAINT_QUARTZ.id])

        assertEquals(10, resultUser[Item.CRYSTALLIZED_BLOOD.id])
        assertNull(resultContainer[Item.CRYSTALLIZED_BLOOD.id])

        assertEquals(10, resultUser[Item.ELDER_SIGN.id])
        assertNull(resultContainer[Item.ELDER_SIGN.id])

        assertEquals(10, resultUser[Item.CORRUPTED_TOPAZ.id])
        assertNull(resultContainer[Item.CORRUPTED_TOPAZ.id])

        assertEquals(5, resultUser[Item.HIGGS_BOSON.id])
        assertNull(resultContainer[Item.HIGGS_BOSON.id])

        assertEquals(5, resultUser[Item.INNSMOUTH_WATER.id])
        assertNull(resultContainer[Item.INNSMOUTH_WATER.id])
    }

    @Test
    fun mixed() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD.id, 0),
            InventoryCell(Item.ELDER_SIGN.id, 3),
            InventoryCell(Item.CORRUPTED_TOPAZ.id, 10),
            InventoryCell(Item.HIGGS_BOSON.id, 3),
            InventoryCell(Item.INNSMOUTH_WATER.id, 3),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertNull(resultContainer[Item.SAINT_QUARTZ.id])

        assertNull(resultUser[Item.CRYSTALLIZED_BLOOD.id])
        assertEquals(10, resultContainer[Item.CRYSTALLIZED_BLOOD.id])

        assertEquals(3, resultUser[Item.ELDER_SIGN.id])
        assertEquals(7, resultContainer[Item.ELDER_SIGN.id])

        assertEquals(10, resultUser[Item.CORRUPTED_TOPAZ.id])
        assertNull(resultContainer[Item.CORRUPTED_TOPAZ.id])

        assertEquals(3, resultUser[Item.HIGGS_BOSON.id])
        assertEquals(2, resultContainer[Item.HIGGS_BOSON.id])

        assertEquals(3, resultUser[Item.INNSMOUTH_WATER.id])
        assertEquals(2, resultContainer[Item.INNSMOUTH_WATER.id])
    }

    @Test
    fun tryToCheat() {
        val newInventoryContent = listOf(
            InventoryCell(Item.MASK.id, 3),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(null, resultUser[Item.MASK.id])
        assertFalse(
            updateContainerRequestGameData.sortedUserInventory.any {
                it.itemId == Item.MASK.id && it.number > 0
            }
        )
    }

    @Test
    fun tryToCheat2() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 100),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertEquals(10,
            updateContainerRequestGameData.sortedUserInventory.filter {
                it.itemId == Item.SAINT_QUARTZ.id
            }.sumOf {
                it.number
            }
        )
    }

    @Test
    fun sortedUserInventory() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD.id, 0),
            InventoryCell(Item.ELDER_SIGN.id, 0),
            InventoryCell(Item.CORRUPTED_TOPAZ.id, 10),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertNull(resultContainer[Item.SAINT_QUARTZ.id])

        assertNull(resultUser[Item.CRYSTALLIZED_BLOOD.id])
        assertEquals(10, resultContainer[Item.CRYSTALLIZED_BLOOD.id])

        assertNull(resultUser[Item.ELDER_SIGN.id])
        assertEquals(10, resultContainer[Item.ELDER_SIGN.id])

        assertEquals(10, resultUser[Item.CORRUPTED_TOPAZ.id])
        assertNull(resultContainer[Item.CORRUPTED_TOPAZ.id])

        assertNull(resultUser[Item.HIGGS_BOSON.id])
        assertEquals(5, resultContainer[Item.HIGGS_BOSON.id])

        assertNull(resultUser[Item.INNSMOUTH_WATER.id])
        assertEquals(5, resultContainer[Item.INNSMOUTH_WATER.id])

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(4, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[0].itemId)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.PURE_NOTHING.id, updateContainerRequestGameData.sortedUserInventory[1].itemId)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.PURE_NOTHING.id, updateContainerRequestGameData.sortedUserInventory[2].itemId)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.CORRUPTED_TOPAZ.id, updateContainerRequestGameData.sortedUserInventory[3].itemId)
    }

    @Test
    fun sortedUserInventory2() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 3),
            InventoryCell(Item.CRYSTALLIZED_BLOOD.id, 0),
            InventoryCell(Item.SAINT_QUARTZ.id, 6),
            InventoryCell(Item.ELDER_SIGN.id, 0),
            InventoryCell(Item.CORRUPTED_TOPAZ.id, 10),
            InventoryCell(Item.SAINT_QUARTZ.id, 1),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertNull(resultContainer[Item.SAINT_QUARTZ.id])

        assertNull(resultUser[Item.CRYSTALLIZED_BLOOD.id])
        assertEquals(10, resultContainer[Item.CRYSTALLIZED_BLOOD.id])

        assertNull(resultUser[Item.ELDER_SIGN.id])
        assertEquals(10, resultContainer[Item.ELDER_SIGN.id])

        assertEquals(10, resultUser[Item.CORRUPTED_TOPAZ.id])
        assertNull(resultContainer[Item.CORRUPTED_TOPAZ.id])

        assertNull(resultUser[Item.HIGGS_BOSON.id])
        assertEquals(5, resultContainer[Item.HIGGS_BOSON.id])

        assertNull(resultUser[Item.INNSMOUTH_WATER.id])
        assertEquals(5, resultContainer[Item.INNSMOUTH_WATER.id])

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(3, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[0].itemId)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.PURE_NOTHING.id, updateContainerRequestGameData.sortedUserInventory[1].itemId)

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[2].itemId)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.PURE_NOTHING.id, updateContainerRequestGameData.sortedUserInventory[3].itemId)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[4].number)
        assertEquals(Item.CORRUPTED_TOPAZ.id, updateContainerRequestGameData.sortedUserInventory[4].itemId)

        assertEquals(1, updateContainerRequestGameData.sortedUserInventory[5].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[5].itemId)
    }

    @Test
    fun sortedUserInventoryTryToCheat() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ.id, 2),
            InventoryCell(Item.SAINT_QUARTZ.id, 2),
            InventoryCell(Item.SAINT_QUARTZ.id, 2),
            InventoryCell(Item.SAINT_QUARTZ.id, 2),
            InventoryCell(Item.SAINT_QUARTZ.id, 2),
            InventoryCell(Item.SAINT_QUARTZ.id, 2), //2*6 = 12, so it is more then in container (10)
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser[Item.SAINT_QUARTZ.id])
        assertNull(resultContainer[Item.SAINT_QUARTZ.id])

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[0].itemId)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[1].itemId)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[2].itemId)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[3].itemId)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[4].number)
        assertEquals(Item.SAINT_QUARTZ.id, updateContainerRequestGameData.sortedUserInventory[4].itemId)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[5].number)
        assertEquals(Item.PURE_NOTHING.id, updateContainerRequestGameData.sortedUserInventory[5].itemId) //nothing here
    }

    private fun executeRequest(newInventoryContent: List<InventoryCell>): Pair<Data, NettyTickRequestMessageDataHolder> {
        val data = prepareDefaultData(newInventoryContent)

        val requestMessage = UpdateContainerRequestMessage(
            actionId = 1000,
            externalInventoryId = data.redisContainer.containerId,
            newInventoryContent = newInventoryContent,
            type = "CloseContainerRequestMessage",
            close = true,
            baseRequestData = BaseRequestData(
                100L,
                UserPosition(
                    data.gameUser.x,
                    data.gameUser.y,
                    data.gameUser.z,
                )
            )
        )

        val request = NettyTickRequestMessageDataHolder(
            nettyRequestMessage = requestMessage,
            channelId = "channel_id",
            userAccount = data.requestUserAccount,
            gameSession = data.gameSession,
            userRole = data.user,
            requestProcessData = data.oldContainer,
            lastExecutedAction = ExecutedAction(1000, true, "")
        )

        updateContainerRequestProcessor.process(
            request,
            data.globalGameData,
            emptyList()
        )
        return data to request
    }

    private fun prepareDefaultData(newInventoryContent: List<InventoryCell>): Data {
        val requestUserAccount = UserAccount(nickName = "test").apply {
            id = 1L
            nickName = "user"
            email = "email"
            password = "password"
            role = setOf(
                Role(
                    id = 1,
                    name = RoleName.USER.securityValue
                )
            )
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
            godId = 1,
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
            y = 50.0,
            z = 50.0
        )
        val inContainerItems = createContainersItems()

        val redisContainer = RedisContainer(
            id = "${gameSession.id}::${container.id}",
            containerId = container.id!!,
            gameId = gameSession.id!!,
            holdingUser = 1,
            state = MapObjectState.HOLD,
            x = container.x,
            y = container.y,
            z = container.z,
            interactionRadius = container.interactionRadius,
            items = inContainerItems
        )

        val oldUserItems = createOldUserItems()

        val gameUser = RedisGameUser(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            userId = user.id!!,
            nickName = "test user",
            role = RoleTypeInGame.INVESTIGATOR,
            classInGame = ClassInGame.MIND_HEALER,
            gameId = gameSession.id!!,
            x = container.x,
            y = container.y,
            z = container.z,
            madness = 20.0,
            madnessNotches = listOf(100.0, 300.0, 600.0),
            items = oldUserItems,
            connected = true,
            stateTags = mutableSetOf(),
            callToArms = 1,
            won = null,
            sawTheEndOfTimes = false,
            leftTheGame = false
        )

        val oldContainer = UpdateContainerRequestGameData(
            container = redisContainer,
            sortedUserInventory = newInventoryContent,
            executedSuccessfully = true,
            gameUser = gameUser,
            otherGameUsers = emptyList(),
            visibleOngoingEvents = emptyList(),
            visibleItems = emptyList(),
            ongoingCraftingProcess = emptyList(),
            availableAbilities = emptyList(),
            tick = 100L,
            containers = emptyList(),
            clues = emptyList(),
            inZones = emptyList(),
            crafters = emptyList(),
            userQuestProgresses = emptyList()
        )

        val globalGameData = GlobalGameData(
            game = redisGame,
            altarHolder = RedisAltarHolder(
                id = "altarHolder",
                gameId = redisGame.gameId!!,
                altarHolderId = 0L,
                x = 50.0,
                y = 50.0,
                z = 50.0,
                radius = 20.0,
                lockedGodId = 1,
                itemsForRitual = emptyMap(),
                itemsIdToAltarId = emptyMap(),
                itemsOnAltars = TODO(),
                state = TODO()
            ),
            users = mapOf(gameUser.userId to gameUser),
            containers = mapOf(redisContainer.containerId to redisContainer),
            timeEvents = emptyList(),
            crafters = emptyMap()
        )
        val data = Data(redisContainer, gameUser, requestUserAccount, gameSession, user, oldContainer, globalGameData)
        return data
    }

    private fun createOldUserItems(): MutableMap<Int, Int> {
        return mutableMapOf(
            Item.SAINT_QUARTZ.id to 5,
            Item.CRYSTALLIZED_BLOOD.id to 5,
            Item.ELDER_SIGN.id to 5,
            Item.CORRUPTED_TOPAZ.id to 5,
            Item.HIGGS_BOSON.id to 5,
        )
    }

    private fun createContainersItems(): MutableMap<Int, Int> {
        return mutableMapOf(
            Item.SAINT_QUARTZ.id to 5,
            Item.CRYSTALLIZED_BLOOD.id to 5,
            Item.ELDER_SIGN.id to 5,
            Item.CORRUPTED_TOPAZ.id to 5,
            Item.INNSMOUTH_WATER.id to 5,
        )
    }

    data class Data(
        val redisContainer: RedisContainer,
        val gameUser: RedisGameUser,
        val requestUserAccount: UserAccount,
        val gameSession: GameSession,
        val user: UserOfGameSession,
        val oldContainer: UpdateContainerRequestGameData,
        val globalGameData: GlobalGameData
    )
}