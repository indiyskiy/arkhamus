package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.OpenContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.otherGameUsersResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.containers.container.OpenContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyResponseMapper(
    private val inventoryHandler: InventoryHandler,
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
        inBetweenEventHolder: InBetweenEventHolder
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
                    inZones = requestProcessData.inZones,
                    clues = requestProcessData.clues
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
                    inZones = requestProcessData.inZones,
                    clues = requestProcessData.clues
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
        clues: List<RedisClue>,
        inZones: List<LevelZone>
    ) = OpenContainerNettyResponse(
        itemsInside = itemsInside,
        state = state,
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
        containers = containers,
        inZones = inZones,
        clues = clues
    )


}