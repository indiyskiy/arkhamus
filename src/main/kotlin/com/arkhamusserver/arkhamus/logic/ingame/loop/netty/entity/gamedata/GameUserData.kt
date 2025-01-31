package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

open class GameUserData(
    val gameUser: InGameGameUser?,
    var otherGameUsers: List<InGameGameUser>,
    var userQuest: List<UserQuestResponse>,
    var inZones: List<LevelZone>,
    var clues: ExtendedCluesResponse,
    var visibleOngoingEvents: List<OngoingEvent>,
    var availableAbilities: List<AbilityOfUserResponse>,
    var ongoingCraftingProcess: List<CraftProcessResponse>,
    var visibleItems: List<InventoryCell>,
    var containers: List<InGameContainer>,
    var crafters: List<InGameCrafter>,
    tick: Long,
) : RequestProcessData(
    tick = tick,
)