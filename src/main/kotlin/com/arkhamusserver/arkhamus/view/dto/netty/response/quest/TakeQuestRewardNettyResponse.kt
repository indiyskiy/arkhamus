package com.arkhamusserver.arkhamus.view.dto.netty.response.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToClueResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.convertToLevelZoneResponses
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.*

class TakeQuestRewardNettyResponse(
    var questInfo: QuestInfoResponse?,
    tick: Long,
    userId: Long,
    myGameUser: MyGameUserResponse,
    otherGameUsers: List<GameUserResponse>,
    ongoingEvents: List<OngoingEventResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    availableAbilities: List<AbilityOfUserResponse>,
    userInventory: List<InventoryCell>,
    containers: List<ContainerState>,
    crafters: List<CrafterState>,
    inZones: List<LevelZone>,
    clues: List<RedisClue>,
) : NettyResponse(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = otherGameUsers,
    ongoingEvents = ongoingEvents,
    ongoingCraftingProcess = ongoingCraftingProcess,
    availableAbilities = availableAbilities,
    userInventory = userInventory,
    containers = containers,
    crafters = crafters,
    inZones = inZones.convertToLevelZoneResponses(),
    clues = clues.convertToClueResponses(),
    type = TakeQuestRewardNettyResponse::class.java.simpleName,
)
