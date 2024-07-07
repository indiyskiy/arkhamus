package com.arkhamusserver.arkhamus.view.dto.netty.response.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

class OpenContainerNettyResponse(
    var itemsInside: List<InventoryCell> = emptyList(),
    var state: MapObjectState,
    var holdingUser: Long?,
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
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    userInventory = userInventory,
    containers = containers.convertToContainerInfo(),
    inZones = inZones.convertToLevelZoneResponses(),
    clues = clues.convertToClueResponses(),
    type = OpenContainerNettyResponse::class.java.simpleName
)