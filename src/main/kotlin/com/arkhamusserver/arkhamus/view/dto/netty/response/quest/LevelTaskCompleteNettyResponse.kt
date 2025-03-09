package com.arkhamusserver.arkhamus.view.dto.netty.response.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse

import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToLevelZoneResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class LevelTaskCompleteNettyResponse(
    var questInfo: QuestInfoResponse?,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<GameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    shortTimeEvents: List<ShortTimeEventResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    userInventory: List<InventoryCellResponse>,
    containers: List<ContainerStateResponse>,
    crafters: List<CrafterState>,
    inZones: List<LevelZone>,
    doors: List<DoorResponse>,
    clues: ExtendedCluesResponse,
    lanterns: List<LanternData>,
    easyVoteSpots: List<EasyVoteSpotResponse>
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    shortTimeEvents = shortTimeEvents,
    ongoingCraftingProcess = ongoingCraftingProcess,
    availableAbilities = availableAbilities,
    userInventory = userInventory,
    containers = containers,
    crafters = crafters,
    inZones = inZones.convertToLevelZoneResponses(),
    doors = doors,
    lanterns = lanterns,
    clues = clues,
    easyVoteSpots = easyVoteSpots,
    type = LevelTaskCompleteNettyResponse::class.java.simpleName,
)
