package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.ContainerDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.CrafterDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.OpenCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.*
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter.OpenCrafterNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class OpenCrafterNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
) : NettyResponseMapper {

    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenCrafterRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): OpenCrafterNettyResponse {
        with(requestProcessData as OpenCrafterRequestGameData) {
            val mappedItem = this.crafter.items.map {
                itemMap[it.key]!! to it.value
            }
            val containerState = requestProcessData.crafter.state
            val containerHoldingUserId = requestProcessData.crafter.holdingUser
            val itemsInside = mappedItem.map {
                InventoryCell(it.first.id).apply {
                    this.number = it.second
                }
            }
            if (
                containerState == MapObjectState.HOLD &&
                containerHoldingUserId == user.id
            ) {
                return buildCrafter(
                    itemsInside = itemsInside,
                    gameData = requestProcessData,
                    user = user,
                    gameUser = requestProcessData.gameUser!!,
                    availableAbilities = requestProcessData.availableAbilities,
                    ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                    visibleItems = requestProcessData.visibleItems,
                    state = containerState,
                    containerHoldingUserId = containerHoldingUserId,
                    containers = requestProcessData.containers,
                    crafters = requestProcessData.crafters,
                    inZones = requestProcessData.inZones,
                    clues = requestProcessData.clues,
                    userQuestProgresses = requestProcessData.userQuestProgresses,
                    levelGeometryData = globalGameData.levelGeometryData
                )
            } else {
                return buildCrafter(
                    itemsInside = emptyList(),
                    gameData = requestProcessData,
                    user = user,
                    gameUser = requestProcessData.gameUser!!,
                    availableAbilities = requestProcessData.availableAbilities,
                    ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                    visibleItems = requestProcessData.visibleItems,
                    state = containerState,
                    containerHoldingUserId = containerHoldingUserId,
                    containers = requestProcessData.containers,
                    crafters = requestProcessData.crafters,
                    inZones = requestProcessData.inZones,
                    clues = requestProcessData.clues,
                    userQuestProgresses = requestProcessData.userQuestProgresses,
                    levelGeometryData = globalGameData.levelGeometryData
                )
            }
        }
    }

    private fun buildCrafter(
        itemsInside: List<InventoryCell> = emptyList(),
        gameData: OpenCrafterRequestGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
        state: MapObjectState,
        containerHoldingUserId: Long?,
        containers: List<RedisContainer>,
        crafters: List<RedisCrafter>,
        clues: List<RedisClue>,
        userQuestProgresses: List<RedisUserQuestProgress>,
        inZones: List<LevelZone>,
        levelGeometryData: LevelGeometryData
    ) = OpenCrafterNettyResponse(
        itemsInside = itemsInside,
        state = state,
        crafterType = gameData.crafter.crafterType,
        holdingUser = containerHoldingUserId,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponse(gameUser, userQuestProgresses),
        otherGameUsers = otherGameUsersDataHandler.map(
            myUser = gameUser,
            gameData.otherGameUsers,
            levelGeometryData
        ),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        availableAbilities = availableAbilities,
        ongoingCraftingProcess = ongoingCraftingProcess,
        userInventory = visibleItems,
        containers = containersDataHandler.map(
            gameUser,
            containers,
            levelGeometryData
        ),
        crafters = craftersDataHandler.map(
            gameUser,
            crafters,
            levelGeometryData
        ),
        inZones = inZones,
        clues = clues
    )


    private val itemMap = Item.values().associateBy { it.id }

}