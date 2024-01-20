package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession

data class AuthGameResponse(
    var message: String? = null,
    var userAccount: UserAccount? = null,
    var game: GameSession? = null,
    var userOfTheGame: UserOfGameSession? = null
) : GameResponseMessage