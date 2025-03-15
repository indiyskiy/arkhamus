package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

abstract class NettyResponse(
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponse,
    val otherGameUsers: List<GameUserResponse>,
    val ongoingEvents: List<OngoingEventResponse>,
    val shortTimeEvents: List<ShortTimeEventResponse>,
    val ongoingCraftingProcess: List<CraftProcessResponse>,
    val availableAbilities: List<AbilityOfUserResponse>,
    var userInventory: List<InventoryCellResponse>,
    var containers: List<ContainerStateResponse>,
    var crafters: List<CrafterState>,
    var inZones: List<LevelZoneResponse>,
    var doors: List<DoorResponse>,
    var lanterns: List<LanternData>,
    var easyVoteSpots: List<EasyVoteSpotResponse>,
    var clues: ExtendedCluesResponse,
    var questGivers: List<QuestGiverResponse>,
    var questSteps: List<QuestStepResponse>,
    val type: String
)

fun List<LevelZone>.convertToLevelZoneResponses(): List<LevelZoneResponse> {
    return this.map { levelZone ->
        LevelZoneResponse(levelZone)
    }
}
