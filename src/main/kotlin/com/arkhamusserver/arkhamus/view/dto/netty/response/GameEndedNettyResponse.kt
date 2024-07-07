package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
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
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    userInventory: List<InventoryCell>,
    containers: List<RedisContainer>,
    clues: List<RedisClue>,
    inZones: List<LevelZone>,
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
    type = GameEndedNettyResponse::class.java.simpleName
)