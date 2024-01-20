package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import io.netty.channel.Channel

data class ArkhamusChannel(
    var channelId: String? = null,
    var channel: Channel? = null,
    var userAccount: UserAccount? = null,
    var gameSession: GameSession? = null,
    var userRole: UserOfGameSession? = null,
)
