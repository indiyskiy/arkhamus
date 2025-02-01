package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class QuestGiverOpenRequestProcessData(
    var quest: InGameQuest?,
    var userQuestProgress: InGameUserQuestProgress?,
    var questRewards: List<InGameQuestReward>,
    var canAccept: Boolean,
    var canDecline: Boolean,
    var canFinish: Boolean,
    var questGiverId: Long,
    var rightQuestGiverForAction: Boolean,
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