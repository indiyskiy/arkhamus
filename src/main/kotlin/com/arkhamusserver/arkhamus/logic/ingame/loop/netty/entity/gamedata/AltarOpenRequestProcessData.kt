package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class AltarOpenRequestProcessData(
    var altar: RedisAltar?,
    var altarHolder: RedisAltarHolder?,
    var altarPolling: RedisAltarPolling?,
    var voteProcessOpen: Boolean,
    var canVote: Boolean,
    var canStartVote: Boolean,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<RedisContainer>,
    tick: Long
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    visibleOngoingEvents = visibleOngoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    visibleItems = visibleItems,
    containers = containers,
    tick = tick
)