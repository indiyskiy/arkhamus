package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class GameEndedRequestGameData(
    val gameEnded: Boolean,
    val gameEndReason: GameEndReason?,
    val winners: List<InGameGameUser>?,
    val losers: List<InGameGameUser>?,
    val god: God,
    gameUser: InGameGameUser,
    otherGameUsers: List<InGameGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<InGameContainer>,
    crafters: List<InGameCrafter>,
    tick: Long
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    inZones = emptyList(),
    visibleOngoingEvents = visibleOngoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    visibleItems = visibleItems,
    containers = containers,
    crafters = crafters,
    clues = ExtendedCluesResponse(emptyList(), emptyList()),
    userQuest = emptyList(),
    tick = tick
)