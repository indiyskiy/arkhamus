package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenItemHolderChanges
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class UpdateCrafterNettyResponseMapper(
    val itemsInBetweenHandler: ItemsInBetweenHandler
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == UpdateCrafterGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): UpdateCrafterNettyResponse {
        with(requestProcessData as UpdateCrafterGameData) {
            return build(
                sortedUserInventory = sortedUserInventory.applyInBetween(
                    inBetweenEventHolder.inBetweenItemHolderChanges,
                    user.id!!
                ),
                itemsInside = crafter.items.map {
                    InventoryCell().apply {
                        number = it.value
                        itemId = it.key
                    }
                },
                gameData = this,
                user = user,
                gameUser = gameUser!!,
                availableAbilities = availableAbilities,
                ongoingCraftingProcess = ongoingCraftingProcess,
                containers = requestProcessData.containers
            )
        }
    }

    private fun build(
        sortedUserInventory: List<InventoryCell>,
        gameData: UpdateCrafterGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>,
        ongoingCraftingProcess: List<CraftProcessResponse>,
        itemsInside: List<InventoryCell>,
        containers: List<RedisContainer>
    ) = UpdateCrafterNettyResponse(
        sortedUserInventory = sortedUserInventory,
        itemsInside = itemsInside,
        state = gameData.crafter.state,
        holdingUser = gameData.crafter.holdingUser,
        userInventory = sortedUserInventory,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsersResponseMessage(),
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        },
        containers = containers,
        ongoingCraftingProcess = ongoingCraftingProcess,
        availableAbilities = availableAbilities,
        executedSuccessfully = true,
        firstTime = true
    )

    private fun List<InventoryCell>.applyInBetween(
        inBetweenItemHolderChanges: MutableList<InBetweenItemHolderChanges>,
        userId: Long
    ): List<InventoryCell> {
        return itemsInBetweenHandler.applyInBetween(this, inBetweenItemHolderChanges, userId)
    }
}


