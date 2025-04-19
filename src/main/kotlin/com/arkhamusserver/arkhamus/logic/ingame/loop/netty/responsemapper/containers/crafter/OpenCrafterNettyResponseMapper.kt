package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.OpenCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter.OpenCrafterNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.springframework.stereotype.Component

@Component
class OpenCrafterNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val questProgressHandler: QuestProgressHandler,
    private val userStatusMapper: UserInGameStatusMapper,
    private val altarsDataHandler: AltarsDataHandler,
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
            val mappedItem = this.crafter.items
            val containerState = requestProcessData.crafter.state
            val containerHoldingUserId = requestProcessData.crafter.holdingUser
            val itemsInside = mappedItem.map {
                InventoryCell(it.item, it.number)
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
                    userQuestProgresses = requestProcessData.userQuest,
                    levelGeometryData = globalGameData.levelGeometryData,
                    shortTimeEvents = globalGameData.shortTimeEvents,
                    globalGameData = globalGameData
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
                    userQuestProgresses = requestProcessData.userQuest,
                    levelGeometryData = globalGameData.levelGeometryData,
                    shortTimeEvents = globalGameData.shortTimeEvents,
                    globalGameData = globalGameData
                )
            }
        }
    }

    private fun buildCrafter(
        itemsInside: List<InventoryCell> = emptyList(),
        gameData: OpenCrafterRequestGameData,
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
        inZones: List<LevelZone>,
        levelGeometryData: LevelGeometryData,
        shortTimeEvents: List<InGameShortTimeEvent>,
        globalGameData: GlobalGameData
    ) = OpenCrafterNettyResponse(
        itemsInside = itemsInside.mapCellsToResponse(),
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
        shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
            shortTimeEvents,
            gameUser,
            inZones,
            globalGameData
        ),
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
        questGivers = questProgressHandler.mapQuestGivers(
            userQuestProgresses,
            gameUser,
            globalGameData
        ),
        questSteps = questProgressHandler.mapSteps(
            userQuestProgresses,
            gameUser,
            globalGameData,
        ),
        easyVoteSpots = voteSpotInfoMapper.mapEasy(
            gameUser,
            globalGameData.voteSpots,
            globalGameData.levelGeometryData
        ),
        altars = altarsDataHandler.mapAltars(globalGameData.altars, gameUser, globalGameData),
        statuses = userStatusMapper.mapStatuses(gameUser, globalGameData),
    )
}