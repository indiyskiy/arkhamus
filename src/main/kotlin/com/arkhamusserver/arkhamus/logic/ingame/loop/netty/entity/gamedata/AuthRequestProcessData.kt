package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class AuthRequestProcessData(
    var message: String? = null,
    var userAccount: UserAccount? = null,
    var game: GameSession? = null,
    var userOfTheGame: UserOfGameSession? = null,
    gameUser: RedisGameUser?,
    otherGameUsers: List<RedisGameUser> = emptyList(),
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    inZones = emptyList(),
    visibleOngoingEvents = emptyList(),
    availableAbilities = emptyList(),
    visibleItems = emptyList(),
    ongoingCraftingProcess = emptyList(),
    containers = emptyList(),
    clues = emptyList(),
    tick = -1L
)