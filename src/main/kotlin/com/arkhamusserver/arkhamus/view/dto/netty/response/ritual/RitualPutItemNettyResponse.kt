package com.arkhamusserver.arkhamus.view.dto.netty.response.ritual

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.RitualGoingDataResponse

class RitualPutItemNettyResponse(
    private val itemId: Int?,
    private val itemNumber: Int?,
    private val ritualGoingData: RitualGoingDataResponse,
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
    type = RitualPutItemNettyResponse::class.java.simpleName,
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}
