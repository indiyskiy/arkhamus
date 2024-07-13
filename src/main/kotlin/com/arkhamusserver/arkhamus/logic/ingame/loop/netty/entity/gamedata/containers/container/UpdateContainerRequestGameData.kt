package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ActionProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisClue
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class UpdateContainerRequestGameData(
    var container: RedisContainer,
    var sortedUserInventory: List<InventoryCell>,
    var executedSuccessfully: Boolean,
    inZones: List<LevelZone>,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<RedisContainer>,
    crafters: List<RedisCrafter>,
    clues: List<RedisClue>,
    tick: Long
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    inZones = inZones,
    visibleOngoingEvents = visibleOngoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    visibleItems = visibleItems,
    containers = containers,
    crafters = crafters,
    clues = clues,
    tick = tick
), ActionProcessData {
    override fun executedSuccessfully(): Boolean {
        return executedSuccessfully
    }

    override fun updateExecutedSuccessfully(executedSuccessfully: Boolean) {
        this.executedSuccessfully = executedSuccessfully
    }
}