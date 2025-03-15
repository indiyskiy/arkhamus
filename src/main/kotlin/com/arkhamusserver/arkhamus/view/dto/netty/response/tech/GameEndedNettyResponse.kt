package com.arkhamusserver.arkhamus.view.dto.netty.response.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToLevelZoneResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

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
    userInventory: List<InventoryCellResponse>,
    containers: List<ContainerStateResponse>,
    crafters: List<CrafterState>,
    clues: ExtendedCluesResponse,
    inZones: List<LevelZone>,
    doors: List<DoorResponse>,
    lanterns: List<LanternData>,
    questGivers: List<QuestGiverResponse>,
    questSteps: List<QuestStepResponse>,
    easyVoteSpots: List<EasyVoteSpotResponse>
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
    clues = clues,
    easyVoteSpots = easyVoteSpots,
    questGivers = questGivers,
    questSteps = questSteps,
    type = GameEndedNettyResponse::class.java.simpleName
)