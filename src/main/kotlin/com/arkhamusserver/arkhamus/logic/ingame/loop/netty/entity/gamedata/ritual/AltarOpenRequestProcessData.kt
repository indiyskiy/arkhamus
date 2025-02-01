package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class AltarOpenRequestProcessData(
    var altar: InGameAltar?,
    var altarHolder: InGameAltarHolder?,
    var altarPolling: InGameAltarPolling?,
    var voteProcessOpen: Boolean,
    var canVote: Boolean,
    var canStartVote: Boolean,
    var voteState: MapAltarPollingState,
    var votedForGod: God?,
    var godLocked: God?,
    inZones: List<LevelZone>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    gameUser: InGameUser,
    otherGameUsers: List<InGameUser>,
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
)