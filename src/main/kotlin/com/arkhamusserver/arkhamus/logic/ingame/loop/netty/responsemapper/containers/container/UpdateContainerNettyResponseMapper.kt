package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.UpdateContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ItemsInBetweenHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.container.UpdateContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse
import org.springframework.stereotype.Component

@Component
class UpdateContainerNettyResponseMapper(
    private val itemsInBetweenHandler: ItemsInBetweenHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val questProgressHandler: QuestProgressHandler,
    private val userStatusMapper: UserInGameStatusMapper
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == UpdateContainerRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): UpdateContainerNettyResponse {
        with(requestProcessData as UpdateContainerRequestGameData) {
            return build(
                sortedUserInventory = sortedUserInventory.applyInBetween(
                    inBetweenEventHolder.inBetweenItemHolderChanges,
                    user.id!!
                ).mapCellsToResponse(),
                gameData = this,
                user = user,
                gameUser = gameUser!!,
                availableAbilities = availableAbilities,
                ongoingCraftingProcess = ongoingCraftingProcess,
                itemsInside = container.items.map {
                    InventoryCell().apply {
                        number = it.number
                        item = it.item
                    }
                }.mapCellsToResponse(),
                containers = requestProcessData.containers,
                crafters = requestProcessData.crafters,
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues,
                userQuestProgresses = requestProcessData.userQuest,
                levelGeometryData = globalGameData.levelGeometryData,
                shortTimeEvents = globalGameData.shortTimeEvents,
                globalGameData = globalGameData,
            )
        }
    }

    private fun build(
        sortedUserInventory: List<InventoryCellResponse>,
        gameData: UpdateContainerRequestGameData,
        user: UserAccount,
        gameUser: InGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        itemsInside: List<InventoryCellResponse>,
        containers: List<InGameContainer>,
        crafters: List<InGameCrafter>,
        inZones: List<LevelZone>,
        clues: ExtendedCluesResponse,
        userQuestProgresses: List<UserQuestResponse>,
        levelGeometryData: LevelGeometryData,
        shortTimeEvents: List<InGameShortTimeEvent>,
        globalGameData: GlobalGameData
    ) = UpdateContainerNettyResponse(
        sortedUserInventory = sortedUserInventory,
        itemsInside = itemsInside,
        state = gameData.container.state,
        holdingUser = gameData.container.holdingUser,
        executedSuccessfully = true,
        firstTime = true,
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
        userInventory = sortedUserInventory,
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
        doors = doorDataHandler.map(
            gameUser,
            globalGameData.doors,
            globalGameData.levelGeometryData
        ),
        clues = clues,
        lanterns = lanternDataHandler.map(
            gameUser,
            globalGameData.lanterns,
            globalGameData.levelGeometryData
        ),
        easyVoteSpots = voteSpotInfoMapper.mapEasy(
            gameUser,
            globalGameData.voteSpots,
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
        statuses = userStatusMapper.mapStatuses(gameUser, globalGameData),
    )

    private fun List<InventoryCell>.applyInBetween(
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        return itemsInBetweenHandler.applyInBetween(this, inBetweenItemHolderChanges, userId)
    }

}