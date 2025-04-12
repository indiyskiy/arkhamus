package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone

import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class HeartbeatNettyResponse(
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<GameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    shortTimeEvents: List<ShortTimeEventResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCellResponse>,
    containers: List<ContainerStateResponse>,
    crafters: List<CrafterState>,
    clues: ExtendedCluesResponse,
    inZones: List<LevelZone>,
    doors: List<DoorResponse>,
    lanterns: List<LanternData>,
    questGivers: List<QuestGiverResponse>,
    questSteps: List<QuestStepResponse>,
    easyVoteSpots: List<EasyVoteSpotResponse>,
    statuses: List<UserStatusResponse>,
    altars: List<EasyAltarResponse>
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
    clues = clues,
    lanterns = lanterns,
    easyVoteSpots = easyVoteSpots,
    questGivers = questGivers,
    questSteps = questSteps,
    statuses = statuses,
    altars = altars,
    type = HeartbeatNettyResponse::class.java.simpleName
)