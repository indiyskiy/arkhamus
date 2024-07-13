package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

abstract class NettyResponse(
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponse,
    val otherGameUsers: List<GameUserResponse>,
    val ongoingEvents: List<OngoingEventResponse>,
    val ongoingCraftingProcess: List<CraftProcessResponse>,
    val availableAbilities: List<AbilityOfUserResponse>,
    var userInventory: List<InventoryCell>,
    var containers: List<ContainerState>,
    var crafters: List<CrafterState>,
    var inZones: List<LevelZoneResponse>,
    var clues: List<ClueResponse>,
    val type: String
)

fun List<LevelZone>.convertToLevelZoneResponses(): List<LevelZoneResponse> {
    return this.map { levelZone ->
        LevelZoneResponse(levelZone)
    }
}

fun List<RedisClue>.convertToClueResponses(): List<ClueResponse> {
    return this.map { redisClue ->
        ClueResponse(redisClue)
    }
}