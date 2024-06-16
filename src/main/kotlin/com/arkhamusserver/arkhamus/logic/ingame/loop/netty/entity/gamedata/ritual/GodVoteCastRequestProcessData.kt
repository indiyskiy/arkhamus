package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ActionProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class GodVoteCastRequestProcessData(
    var votedGod: God?,
    var altar: RedisAltar?,
    var canVote: Boolean,
    var executedSuccessfully: Boolean,
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
), ActionProcessData {
    override fun executedSuccessfully(): Boolean {
        return executedSuccessfully
    }

    override fun updateExecutedSuccessfully(executedSuccessfully: Boolean) {
        this.executedSuccessfully = executedSuccessfully
    }
}