package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.MockInGameDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ExecutedAction
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.UpdateContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container.UpdateContainerRequestProcessor
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Container
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.LevelState
import com.arkhamusserver.arkhamus.model.enums.RoleName
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarHolder
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting
import com.arkhamusserver.arkhamus.view.dto.netty.request.BaseRequestData
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container.UpdateContainerRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp

@SpringBootTest
class UpdateContainerRequestProcessorTest {
    @Autowired
    private lateinit var redisDataAccess: MockInGameDataAccess

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

        assertNull(resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.ELDER_SIGN })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.ELDER_SIGN }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.CORRUPTED_TOPAZ })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.CORRUPTED_TOPAZ }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.HIGGS_BOSON })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.RAGS })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.RAGS }?.number)
    }

    @Test
    fun fullInventory() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 10),
            InventoryCell(Item.ELDER_SIGN, 10),
            InventoryCell(Item.CORRUPTED_TOPAZ, 10),
            InventoryCell(Item.HIGGS_BOSON, 5),
            InventoryCell(Item.RAGS, 5),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ })

        assertEquals(10, resultUser.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD })

        assertEquals(10, resultUser.firstOrNull { it.item == Item.ELDER_SIGN }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.ELDER_SIGN })

        assertEquals(10, resultUser.firstOrNull { it.item == Item.CORRUPTED_TOPAZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.CORRUPTED_TOPAZ })

        assertEquals(5, resultUser.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.HIGGS_BOSON })

        assertEquals(5, resultUser.firstOrNull { it.item == Item.RAGS }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.RAGS })
    }

    @Test
    fun mixed() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 0),
            InventoryCell(Item.ELDER_SIGN, 3),
            InventoryCell(Item.CORRUPTED_TOPAZ, 10),
            InventoryCell(Item.HIGGS_BOSON, 3),
            InventoryCell(Item.RAGS, 3),
        )

        val (data, _) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ })

        assertNull(resultUser.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD }?.number)

        assertEquals(3, resultUser.firstOrNull { it.item == Item.ELDER_SIGN }?.number)
        assertEquals(7, resultContainer.firstOrNull { it.item == Item.ELDER_SIGN }?.number)

        assertEquals(10, resultUser.firstOrNull { it.item == Item.CORRUPTED_TOPAZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.CORRUPTED_TOPAZ })

        assertEquals(3, resultUser.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)
        assertEquals(2, resultContainer.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)

        assertEquals(3, resultUser.firstOrNull { it.item == Item.RAGS }?.number)
        assertEquals(2, resultContainer.firstOrNull { it.item == Item.RAGS }?.number)
    }

    @Test
    fun tryToCheat() {
        val newInventoryContent = listOf(
            InventoryCell(Item.MASK, 3),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(null, resultUser.firstOrNull { it.item == Item.MASK })
        assertFalse(
            updateContainerRequestGameData.sortedUserInventory.any {
                it.item == Item.MASK && it.number > 0
            }
        )
    }

    @Test
    fun tryToCheat2() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 100),
        )
        val (data, requestContainer) = executeRequest(newInventoryContent)
        val resultUser = data.globalGameData.users[1L]!!.items
        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertEquals(
            10,
            updateContainerRequestGameData.sortedUserInventory.filter {
                it.item == Item.SAINT_QUARTZ
            }.sumOf {
                it.number
            }
        )
    }

    @Test
    fun sortedUserInventory() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 10),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 0),
            InventoryCell(Item.ELDER_SIGN, 0),
            InventoryCell(Item.CORRUPTED_TOPAZ, 10),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ })

        assertNull(resultUser.firstOrNull { Item.CRYSTALLIZED_BLOOD == it.item })
        assertEquals(10, resultContainer.firstOrNull { Item.CRYSTALLIZED_BLOOD == it.item }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.ELDER_SIGN })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.ELDER_SIGN }?.number)

        assertEquals(10, resultUser.firstOrNull { it.item == Item.CORRUPTED_TOPAZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.CORRUPTED_TOPAZ })

        assertNull(resultUser.firstOrNull { it.item == Item.HIGGS_BOSON })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.RAGS })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.RAGS }?.number)

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(4, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[0].item)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.PURE_NOTHING, updateContainerRequestGameData.sortedUserInventory[1].item)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.PURE_NOTHING, updateContainerRequestGameData.sortedUserInventory[2].item)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.CORRUPTED_TOPAZ, updateContainerRequestGameData.sortedUserInventory[3].item)
    }


    @Test
    fun sortedUserInventory2() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 3),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 0),
            InventoryCell(Item.SAINT_QUARTZ, 6),
            InventoryCell(Item.ELDER_SIGN, 0),
            InventoryCell(Item.CORRUPTED_TOPAZ, 10),
            InventoryCell(Item.SAINT_QUARTZ, 1),
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ })

        assertNull(resultUser.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.CRYSTALLIZED_BLOOD }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.ELDER_SIGN })
        assertEquals(10, resultContainer.firstOrNull { it.item == Item.ELDER_SIGN }?.number)

        assertEquals(10, resultUser.firstOrNull { it.item == Item.CORRUPTED_TOPAZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.CORRUPTED_TOPAZ })

        assertNull(resultUser.firstOrNull { it.item == Item.HIGGS_BOSON })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.HIGGS_BOSON }?.number)

        assertNull(resultUser.firstOrNull { it.item == Item.RAGS })
        assertEquals(5, resultContainer.firstOrNull { it.item == Item.RAGS }?.number)

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(3, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[0].item)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.PURE_NOTHING, updateContainerRequestGameData.sortedUserInventory[1].item)

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[2].item)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.PURE_NOTHING, updateContainerRequestGameData.sortedUserInventory[3].item)

        assertEquals(10, updateContainerRequestGameData.sortedUserInventory[4].number)
        assertEquals(Item.CORRUPTED_TOPAZ, updateContainerRequestGameData.sortedUserInventory[4].item)

        assertEquals(1, updateContainerRequestGameData.sortedUserInventory[5].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[5].item)
    }

    @Test
    fun sortedUserInventoryTryToCheat() {
        val newInventoryContent = listOf(
            InventoryCell(Item.SAINT_QUARTZ, 2),
            InventoryCell(Item.SAINT_QUARTZ, 2),
            InventoryCell(Item.SAINT_QUARTZ, 2),
            InventoryCell(Item.SAINT_QUARTZ, 2),
            InventoryCell(Item.SAINT_QUARTZ, 2),
            InventoryCell(Item.SAINT_QUARTZ, 2), //2*6 = 12, so it is more than in container (10)
        )

        val (data, requestContainer) = executeRequest(newInventoryContent)

        val resultUser = data.globalGameData.users[1L]!!.items
        val resultContainer = data.globalGameData.containers[1L]!!.items

        assertEquals(10, resultUser.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)
        assertNull(resultContainer.firstOrNull { it.item == Item.SAINT_QUARTZ }?.number)

        val updateContainerRequestGameData = requestContainer.requestProcessData as UpdateContainerRequestGameData

        assertEquals(6, updateContainerRequestGameData.sortedUserInventory.size)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[0].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[0].item)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[1].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[1].item)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[2].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[2].item)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[3].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[3].item)

        assertEquals(2, updateContainerRequestGameData.sortedUserInventory[4].number)
        assertEquals(Item.SAINT_QUARTZ, updateContainerRequestGameData.sortedUserInventory[4].item)

        assertEquals(0, updateContainerRequestGameData.sortedUserInventory[5].number)
        assertEquals(Item.PURE_NOTHING, updateContainerRequestGameData.sortedUserInventory[5].item) //nothing here
    }

    private fun executeRequest(newInventoryContent: List<InventoryCell>): Pair<Data, NettyTickRequestMessageDataHolder> {
        val data = prepareDefaultData(newInventoryContent)

        val requestMessage = UpdateContainerRequestMessage(
            actionId = 1000,
            externalInventoryId = data.inGameContainer.inGameId(),
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
                level = level,
                classesInGame = emptySet()
            ),
            state = GameState.IN_PROGRESS,
            gameType = GameType.SINGLE,
            god = God.AAMON,
            token = "gametoken"
        )
        val inRamGame = InRamGame(
            id = gameSession.id.toString(),
            gameId = gameSession.id!!,
            currentTick = 100L,
            globalTimer = 10000L,
            gameStart = System.currentTimeMillis(),
            state = GameState.PENDING.name,
            god = God.AAMON,
        )

        val user = UserOfGameSession(
            id = 1L,
            userAccount = requestUserAccount,
            gameSession = gameSession,
            host = true,
            roleInGame = RoleTypeInGame.INVESTIGATOR,
            leftTheLobby = false
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

        val inGameContainer = InGameContainer(
            id = "${gameSession.id}::${container.id}",
            containerId = container.id!!,
            gameId = gameSession.id!!,
            holdingUser = 1,
            state = MapObjectState.HOLD,
            x = container.x,
            y = container.y,
            z = container.z,
            interactionRadius = container.interactionRadius,
            items = inContainerItems,
            gameTags = mutableSetOf(),
            containerTags = mutableSetOf(),
            visibilityModifiers = mutableSetOf(),
        )

        val oldUserItems = createOldUserItems()

        val gameUser = InGameUser(
            id = generateRandomId(),
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
            leftTheGame = false,
            madnessDebuffs = mutableSetOf(),
            visibilityModifiers = mutableSetOf(),
            originalSkin = InGameUserSkinSetting(SkinColor.LAVENDER)
        )

        val oldContainer = UpdateContainerRequestGameData(
            container = inGameContainer,
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
            clues = ExtendedCluesResponse(emptyList(), emptyList()),
            inZones = emptyList(),
            crafters = emptyList(),
            userQuestProgresses = emptyList()
        )

        val globalGameData = GlobalGameData(
            game = inRamGame,
            altarHolder = InGameAltarHolder(
                id = "altarHolder",
                gameId = inRamGame.gameId,
                altarHolderId = 0L,
                x = 50.0,
                y = 50.0,
                z = 50.0,
                radius = 20.0,
                lockedGod = God.AAMON,
                itemsForRitual = emptyMap(),
                itemsToAltarId = emptyMap(),
            ),
            users = mapOf(gameUser.userId to gameUser),
            containers = mapOf(inGameContainer.inGameId() to inGameContainer),
            timeEvents = emptyList(),
            crafters = emptyMap()
        )
        val data = Data(inGameContainer, gameUser, requestUserAccount, gameSession, user, oldContainer, globalGameData)
        return data
    }

    private fun createOldUserItems(): List<InventoryCell> {
        return listOf(
            InventoryCell(Item.SAINT_QUARTZ, 5),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 5),
            InventoryCell(Item.ELDER_SIGN, 5),
            InventoryCell(Item.CORRUPTED_TOPAZ, 5),
            InventoryCell(Item.HIGGS_BOSON, 5),
        )
    }

    private fun createContainersItems(): List<InventoryCell> {
        return listOf(
            InventoryCell(Item.SAINT_QUARTZ, 5),
            InventoryCell(Item.CRYSTALLIZED_BLOOD, 5),
            InventoryCell(Item.ELDER_SIGN, 5),
            InventoryCell(Item.CORRUPTED_TOPAZ, 5),
            InventoryCell(Item.RAGS, 5),
        )
    }

    data class Data(
        val inGameContainer: InGameContainer,
        val gameUser: InGameUser,
        val requestUserAccount: UserAccount,
        val gameSession: GameSession,
        val user: UserOfGameSession,
        val oldContainer: UpdateContainerRequestGameData,
        val globalGameData: GlobalGameData
    )
}