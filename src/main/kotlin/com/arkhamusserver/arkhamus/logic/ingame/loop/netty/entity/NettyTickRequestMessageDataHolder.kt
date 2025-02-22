package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage

data class NettyTickRequestMessageDataHolder(
    val nettyRequestMessage: NettyBaseRequestMessage,
    var channelId: String,
    var userAccount: UserAccount,
    val lastExecutedAction: ExecutedAction,
    var gameSession: GameSession? = null,
    var userRole: UserOfGameSession? = null,
    var requestProcessData: RequestProcessData? = null
)