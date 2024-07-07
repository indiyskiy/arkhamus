package com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

class UpdateCrafterNettyResponse(
    sortedUserInventory: List<InventoryCell>,
    itemsInside: List<InventoryCell>,
    state: MapObjectState,
    holdingUser: Long?,
    val executedSuccessfully: Boolean,
    val firstTime: Boolean,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<GameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCell>,
    containers: List<RedisContainer>,
    inZones: List<LevelZone>,
    clues: List<RedisClue>,
) : ExternalInventoryNettyResponse(
    sortedUserInventory = sortedUserInventory,
    itemsInside = itemsInside,
    state = state,
    holdingUser = holdingUser,
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    userInventory = userInventory,
    containers = containers,
    inZones = inZones,
    clues = clues,
    type = UpdateCrafterNettyResponse::class.java.simpleName
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}