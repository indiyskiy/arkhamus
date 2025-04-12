package com.arkhamusserver.arkhamus.view.dto.netty.response.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone

import com.arkhamusserver.arkhamus.view.dto.netty.response.ActionResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse

import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToLevelZoneResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class GodVoteStartNettyResponse(
    val godId: Int?,
    val executedSuccessfully: Boolean,
    val firstTime: Boolean,
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
    questGivers: List<QuestGiverResponse>,
    questSteps: List<QuestStepResponse>,
    easyVoteSpots: List<EasyVoteSpotResponse>,
    altars: List<EasyAltarResponse>,
    statuses: List<UserStatusResponse>
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
    questGivers = questGivers,
    questSteps = questSteps,
    statuses = statuses,
    altars = altars,
    type = GodVoteStartNettyResponse::class.java.simpleName,
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}
