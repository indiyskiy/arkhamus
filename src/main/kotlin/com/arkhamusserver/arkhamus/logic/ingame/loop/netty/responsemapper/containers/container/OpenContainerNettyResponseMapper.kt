package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.ContainerDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.CrafterDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.OpenContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.*
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.container.OpenContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyResponseMapper(
    private val inventoryHandler: InventoryHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
) : NettyResponseMapper {

    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenContainerRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): OpenContainerNettyResponse {
        with(requestProcessData as OpenContainerRequestGameData) {
            val mappedItem = this.container.items.map {
                it.key to it.value
            }
            val containerState = requestProcessData.container.state
            val containerHoldingUserId = requestProcessData.container.holdingUser

            if (containerState == MapObjectState.HOLD && containerHoldingUserId == user.id) {
                val itemsInside = inventoryHandler.mapUsersItems(mappedItem)
                return buildContainer(
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
                return buildContainer(
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

    private fun buildContainer(
        itemsInside: List<InventoryCell>,
        gameData: OpenContainerRequestGameData,
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
        levelGeometryData: LevelGeometryData,
    ) = OpenContainerNettyResponse(
        itemsInside = itemsInside,
        state = state,
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


}