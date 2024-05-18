package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.OpenCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.OpenCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class OpenCrafterNettyResponseMapper : NettyResponseMapper {

    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenCrafterGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): OpenCrafterNettyResponse {
        with(requestProcessData as OpenCrafterGameData) {
            val mappedItem = this.crafter.items.map {
                itemMap[it.key]!! to it.value
            }
            val itemsInside = mappedItem.map {
                InventoryCell(it.first.id).apply {
                    this.number = it.second
                }
            }
            if (requestProcessData.crafter.holdingUser == user.id) {
                return myCrafter(
                    itemsInside,
                    requestProcessData,
                    user,
                    requestProcessData.gameUser!!,
                    requestProcessData.availableAbilities,
                    requestProcessData.ongoingCraftingProcess,
                    requestProcessData.visibleItems
                )
            } else {
                return closedCrafter(
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

    private fun myCrafter(
        itemsInside: List<InventoryCell>,
        gameData: OpenCrafterGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
    ) = OpenCrafterNettyResponse(
        itemsInside = itemsInside,
        state = gameData.crafter.state,
        crafterType = gameData.crafter.crafterType,
        holdingUser = gameData.crafter.holdingUser,
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

    private fun closedCrafter(
        gameData: OpenCrafterGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        visibleItems: List<InventoryCell>,
    ) = OpenCrafterNettyResponse(
        itemsInside = emptyList(),
        state = gameData.crafter.state,
        crafterType = gameData.crafter.crafterType,
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

    private val itemMap = Item.values().associateBy { it.id }

}