package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.OpenCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter.OpenCrafterNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import org.springframework.stereotype.Component

@Component
class OpenCrafterNettyResponseMapper : NettyResponseMapper {

    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenCrafterRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
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
                    containers = requestProcessData.containers
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
                    containers = requestProcessData.containers
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
        containers: List<RedisContainer>
    ) = OpenCrafterNettyResponse(
        itemsInside = itemsInside,
        state = state,
        crafterType = gameData.crafter.crafterType,
        holdingUser = containerHoldingUserId,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponse(gameUser),
        otherGameUsers = gameData.otherGameUsersResponseMessage(),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        availableAbilities = availableAbilities,
        ongoingCraftingProcess = ongoingCraftingProcess,
        userInventory = visibleItems,
        containers = containers
    )


    private val itemMap = Item.values().associateBy { it.id }

}