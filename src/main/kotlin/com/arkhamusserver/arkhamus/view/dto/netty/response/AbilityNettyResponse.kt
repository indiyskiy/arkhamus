package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class AbilityNettyResponse(
    private val abilityId: Int?,
    private val executedSuccessfully: Boolean,
    private val firstTime: Boolean,
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
    lanterns: List<LanternData>,
    clues: ExtendedCluesResponse,
    questGivers: List<QuestGiverResponse>,
    questSteps: List<QuestStepResponse>,
    altars: List<EasyAltarResponse>,
    easyVoteSpots: List<EasyVoteSpotResponse>,
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
    clues = clues,
    lanterns = lanterns,
    easyVoteSpots = easyVoteSpots,
    questGivers = questGivers,
    questSteps = questSteps,
    altars = altars,
    type = AbilityNettyResponse::class.java.simpleName,
    statuses = statuses,
), ActionResponse {
    override fun isExecutedSuccessfully(): Boolean =
        executedSuccessfully

    override fun isFirstTime(): Boolean =
        firstTime
}
