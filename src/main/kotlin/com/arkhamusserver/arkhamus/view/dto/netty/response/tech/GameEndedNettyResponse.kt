package com.arkhamusserver.arkhamus.view.dto.netty.response.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToClueResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToLevelZoneResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

class GameEndedNettyResponse(
    val gameEnded: Boolean,
    val gameEndReason: String?,
    val winners: List<EndOfGameUserResponse>?,
    val losers: List<EndOfGameUserResponse>?,
    val godId: Int,
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
    clues: List<RedisClue>,
    inZones: List<LevelZone>,
    doors: List<DoorResponse>,
    lanterns: List<LanternData>,
) : NettyResponse(
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
    inZones = inZones.convertToLevelZoneResponses(),
    doors = doors,
    lanterns = lanterns,
    clues = clues.convertToClueResponses(),
    type = GameEndedNettyResponse::class.java.simpleName
)