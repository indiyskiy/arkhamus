package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class AbilityNettyResponse(
    private val abilityId: Int?,
    private val executedSuccessfully: Boolean,
    private val firstTime: Boolean,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<NettyGameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    userInventory: List<InventoryCell>,
    containers: List<RedisContainer>
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    ongoingCraftingProcess = ongoingCraftingProcess,
    availableAbilities = availableAbilities,
    userInventory = userInventory,
    containers = containers.convertToContainerInfo(),
    type = AbilityNettyResponse::class.java.simpleName,
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}
