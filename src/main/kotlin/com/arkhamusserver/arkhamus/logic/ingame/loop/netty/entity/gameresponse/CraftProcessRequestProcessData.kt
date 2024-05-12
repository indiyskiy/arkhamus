package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.CraftProcessResponse

class CraftProcessRequestProcessData(
    val recipe: Recipe?,
    val crafter: RedisCrafter?,
    val canBeStarted: Boolean,
    var startedSuccessfully: Boolean,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    visibleItems: List<InventoryCell>,
    tick: Long
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    visibleOngoingEvents = visibleOngoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    visibleItems = visibleItems,
    tick = tick
)