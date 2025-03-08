package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.OpenContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.container.OpenContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyResponseMapper(
    private val inventoryHandler: InventoryHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler
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
            val containerState = requestProcessData.container.state
            val containerHoldingUserId = requestProcessData.container.holdingUser

            if (containerState == MapObjectState.HOLD && containerHoldingUserId == user.id) {
                val itemsInside = inventoryHandler.mapUsersItems(this.container.items)
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
                    userQuestProgresses = requestProcessData.userQuest,
                    shortTimeEvents = globalGameData.shortTimeEvents,
                    levelGeometryData = globalGameData.levelGeometryData,
                    globalGameData = globalGameData
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
                    userQuestProgresses = requestProcessData.userQuest,
                    shortTimeEvents = globalGameData.shortTimeEvents,
                    levelGeometryData = globalGameData.levelGeometryData,
                    globalGameData = globalGameData
                )
            }
        }
    }

    private fun buildContainer(
        itemsInside: List<InventoryCell>,
        gameData: OpenContainerRequestGameData,
        user: UserAccount,
        gameUser: InGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
        state: MapObjectState,
        containerHoldingUserId: Long?,
        containers: List<InGameContainer>,
        crafters: List<InGameCrafter>,
        clues: ExtendedCluesResponse,
        userQuestProgresses: List<UserQuestResponse>,
        shortTimeEvents: List<InGameShortTimeEvent>,
        inZones: List<LevelZone>,
        levelGeometryData: LevelGeometryData,
        globalGameData: GlobalGameData
    ) = OpenContainerNettyResponse(
        itemsInside = itemsInside.mapCellsToResponse(),
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
        shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
            shortTimeEvents,
            gameUser,
            inZones,
            globalGameData
        ),
        availableAbilities = availableAbilities,
        ongoingCraftingProcess = ongoingCraftingProcess,
        userInventory = visibleItems.mapCellsToResponse(),
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
        clues = clues,
        doors = doorDataHandler.map(
            gameUser,
            globalGameData.doors,
            globalGameData.levelGeometryData
        ),
        lanterns = lanternDataHandler.map(
            gameUser,
            globalGameData.lanterns,
            globalGameData.levelGeometryData
        ),
    )

}