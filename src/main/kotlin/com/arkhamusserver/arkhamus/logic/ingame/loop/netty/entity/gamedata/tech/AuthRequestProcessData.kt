package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class AuthRequestProcessData(
    var reason: String? = null,
    var success: Boolean = false,
    var userAccount: UserAccount? = null,
    var game: GameSession? = null,
    var userOfTheGame: UserOfGameSession? = null,
    gameUser: InGameGameUser?,
    otherGameUsers: List<InGameGameUser> = emptyList(),
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    inZones = emptyList(),
    visibleOngoingEvents = emptyList(),
    availableAbilities = emptyList(),
    visibleItems = emptyList(),
    ongoingCraftingProcess = emptyList(),
    containers = emptyList(),
    clues = ExtendedCluesResponse(emptyList(), emptyList()),
    crafters = emptyList(),
    userQuest = emptyList(),
    tick = -1L
)