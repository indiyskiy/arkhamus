package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.OpenContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyResponseMapper : NettyResponseMapper {

    private val itemMap = Item.values().associateBy { it.id }
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenContainerGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): OpenContainerNettyResponse {
        with(requestProcessData as OpenContainerGameData) {
            val mappedItem = this.container.items.map {
                itemMap[it.key]!! to it.value
            }
            val itemsInside = mappedItem.map {
                InventoryCell(it.first.id).apply {
                    this.number = it.second
                }
            }
            if (requestProcessData.container.holdingUser == user.id) {
                return myContainer(
                    itemsInside,
                    requestProcessData,
                    user,
                    requestProcessData.gameUser!!,
                    requestProcessData.availableAbilities,
                    requestProcessData.ongoingCraftingProcess,
                    requestProcessData.visibleItems
                )
            } else {
                return closedContainer(
                    requestProcessData,
                    user,
                    requestProcessData.gameUser!!,
                    requestProcessData.availableAbilities,
                    requestProcessData.ongoingCraftingProcess,
                    requestProcessData.visibleItems
                )
            }
        }
    }

    private fun myContainer(
        itemsInside: List<InventoryCell>,
        gameData: OpenContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
    ) = OpenContainerNettyResponse(
        itemsInside = itemsInside,
        state = gameData.container.state,
        holdingUser = gameData.container.holdingUser,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsersResponseMessage(),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        availableAbilities = availableAbilities,
        ongoingCraftingProcess = ongoingCraftingProcess,
        userInventory = visibleItems
    )

    private fun closedContainer(
        gameData: OpenContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
    ) = OpenContainerNettyResponse(
        itemsInside = emptyList(),
        state = gameData.container.state,
        holdingUser = null,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsersResponseMessage(),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        availableAbilities = availableAbilities,
        ongoingCraftingProcess = ongoingCraftingProcess,
        userInventory = visibleItems
    )

}