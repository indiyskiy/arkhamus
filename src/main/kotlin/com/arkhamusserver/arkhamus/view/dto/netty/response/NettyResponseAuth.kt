package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.AuthState

class NettyResponseAuth(
    val message: AuthState = AuthState.FAIL,
    tick: Long = -1,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    allGameUsers: List<NettyGameUserResponseMessage>,
) : NettyResponseMessage(
    tick = tick,
    userId = userId,
    myGameUser = myGameUser,
    otherGameUsers = allGameUsers,
    ongoingEvents = emptyList(),
    availableAbilities = emptyList(),
    userInventory = emptyList(),
    type = NettyResponseAuth::class.java.simpleName
)