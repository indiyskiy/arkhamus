package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import io.netty.channel.Channel

data class ArkhamusChannel(
    var channelId: String,
    var channel: Channel,
    var userAccount: UserAccount? = null,
    var gameSession: GameSession? = null,
    var userOfGameSession: UserOfGameSession? = null,
)
