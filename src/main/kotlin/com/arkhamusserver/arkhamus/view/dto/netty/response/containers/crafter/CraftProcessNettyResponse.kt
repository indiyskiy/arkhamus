package com.arkhamusserver.arkhamus.view.dto.netty.response.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.view.dto.netty.response.ActionResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.ExternalInventoryNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

class CraftProcessNettyResponse(
    var recipeId: Int?,
    var crafterId: Long?,
    val executedSuccessfully: Boolean,
    val firstTime: Boolean,
    sortedUserInventory: List<InventoryCell>,
    itemsInside: List<InventoryCell>,
    state: MapObjectState,
    holdingUser: Long?,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<GameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    shortTimeEvents: List<ShortTimeEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCell>,
    containers: List<ContainerState>,
    crafters: List<CrafterState>,
    inZones: List<LevelZone>,
    doors: List<DoorResponse>,
    clues: List<RedisClue>,
    lanterns: List<LanternData>,
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
    shortTimeEvents = shortTimeEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    userInventory = userInventory,
    containers = containers,
    crafters = crafters,
    inZones = inZones,
    doors = doors,
    clues = clues,
    lanterns = lanterns,
    type = CraftProcessNettyResponse::class.java.simpleName
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}