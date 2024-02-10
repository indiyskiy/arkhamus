package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

data class NettyTickRequestMessageContainer(
    val nettyRequestMessage: NettyBaseRequestMessage,
    var channelId: String,
    var userAccount: UserAccount,
    var gameSession: GameSession? = null,
    var userRole: UserOfGameSession? = null,
    val registrationTime: Long = System.currentTimeMillis(),
)