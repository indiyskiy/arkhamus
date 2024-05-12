package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.UpdateContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class UpdateContainerNettyResponseMapper(
    val itemsInBetweenHandler: ItemsInBetweenHandler
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == UpdateContainerGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): UpdateContainerNettyResponse {
        with(requestProcessData as UpdateContainerGameData) {
            return build(
                sortedInventory = (sortedInventory ?: emptyList()).applyInBetween(
                    inBetweenEventHolder.inBetweenItemHolderChanges,
                    user.id!!
                ),
                gameData = this,
                user = user,
                gameUser = gameUser!!,
                availableAbilities = availableAbilities,
                ongoingCraftingProcess = ongoingCraftingProcess,
                itemsInside = container.items.map {
                    InventoryCell().apply {
                        number = it.value
                        itemId = it.key
                    }
                }
            )
        }
    }

    private fun build(
        sortedInventory: List<InventoryCell>,
        gameData: UpdateContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        itemsInside: List<InventoryCell>
    ) = UpdateContainerNettyResponse(
        sortedUserInventory = sortedInventory,
        itemsInside = itemsInside,
        state = gameData.container.state,
        holdingUser = gameData.container.holdingUser,
        userInventory = sortedInventory,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsers.map {
            NettyGameUserResponseMessage(
                id = it.userId,
                nickName = it.nickName,
                x = it.x,
                y = it.y
            )
        },
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        ongoingCraftingProcess = ongoingCraftingProcess,
        availableAbilities = availableAbilities
    )

    private fun List<InventoryCell>.applyInBetween(
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        return itemsInBetweenHandler.applyInBetween(this, inBetweenItemHolderChanges, userId)
    }

}