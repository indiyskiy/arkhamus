package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.ContainerDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.CrafterDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.DoorDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.LanternDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.CraftProcessRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ItemsInBetweenHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter.CraftProcessNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class CraftProcessNettyResponseMapper(
    private val itemsInBetweenHandler: ItemsInBetweenHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == CraftProcessRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): CraftProcessNettyResponse {
        (requestProcessData as CraftProcessRequestProcessData).let {
            return CraftProcessNettyResponse(
                recipeId = it.recipe?.recipeId,
                crafterId = it.crafter?.inGameId(),
                executedSuccessfully = it.executedSuccessfully,
                firstTime = true,
                sortedUserInventory = (it.sortedUserInventory).applyInBetween(
                    inBetweenEventHolder.inBetweenItemHolderChanges,
                    user.id!!
                ),
                itemsInside = it.crafter?.items?.map {
                    InventoryCell().apply {
                        number = it.value
                        itemId = it.key
                    }
                } ?: emptyList(),
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!, it.userQuest),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = it.gameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
                    globalGameData.shortTimeEvents,
                    it.gameUser,
                    it.inZones,
                    globalGameData
                ),
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                holdingUser = it.crafter!!.holdingUser,
                state = it.crafter?.state ?: MapObjectState.DISABLED,
                userInventory = requestProcessData.visibleItems,
                containers = containersDataHandler.map(
                    it.gameUser,
                    it.containers,
                    globalGameData.levelGeometryData
                ),
                crafters = craftersDataHandler.map(
                    it.gameUser,
                    it.crafters,
                    globalGameData.levelGeometryData
                ),
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues,
                doors = doorDataHandler.map(
                    it.gameUser,
                    globalGameData.doorsByZoneId.values.flatten(),
                    globalGameData.levelGeometryData
                ),
                lanterns = lanternDataHandler.map(
                    it.gameUser,
                    globalGameData.lanterns,
                    globalGameData.levelGeometryData
                ),
            )
        }
    }

    private fun List<InventoryCell>.applyInBetween(
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        return itemsInBetweenHandler.applyInBetween(this, inBetweenItemHolderChanges, userId)
    }
}