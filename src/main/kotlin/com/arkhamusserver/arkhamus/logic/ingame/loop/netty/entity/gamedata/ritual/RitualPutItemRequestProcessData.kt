package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.ItemNotch
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ActionProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse

class RitualPutItemRequestProcessData(
    var item: Item?,
    var itemNumber: Int,
    var canPut: Boolean,
    var currentGameTime: Long,
    var ritualEvent: RedisTimeEvent?,
    var altarHolder: RedisAltarHolder?,
    var usersInRitual: List<RedisGameUser>,
    var executedSuccessfully: Boolean,
    var currentStepItem: Item?,
    var notches: List<ItemNotch>,
    inZones: List<LevelZone>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<RedisContainer>,
    crafters: List<RedisCrafter>,
    clues: List<RedisClue>,
    userQuestProgresses: List<UserQuestResponse>,
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
    userQuest = userQuestProgresses,
    tick = tick
), ActionProcessData {
    override fun executedSuccessfully(): Boolean {
        return executedSuccessfully
    }

    override fun updateExecutedSuccessfully(executedSuccessfully: Boolean) {
        this.executedSuccessfully = executedSuccessfully
    }
}