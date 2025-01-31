package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ActionProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.InGameAltar
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class GodVoteStartRequestProcessData(
    var votedGod: God?,
    var altar: InGameAltar?,
    var canBeStarted: Boolean,
    var executedSuccessfully: Boolean,
    inZones: List<LevelZone>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    gameUser: InGameGameUser,
    otherGameUsers: List<InGameGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<InGameContainer>,
    crafters: List<InGameCrafter>,
    clues: ExtendedCluesResponse,
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