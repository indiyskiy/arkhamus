package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

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
) : GameUserData(gameUser, otherGameUsers, emptyList(), emptyList(), -1L)