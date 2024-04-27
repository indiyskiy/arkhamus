package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.AuthState

class NettyResponseAuth(
    val message: AuthState = AuthState.FAIL,
    tick: Long = -1,
    userId: Long,
    myGameUser: MyGameUserResponseMessage,
    allGameUsers: List<NettyGameUserResponseMessage>,
) : NettyResponseMessage(
    tick,
    userId,
    myGameUser,
    allGameUsers,
    emptyList(),
    emptyList(),
    NettyResponseAuth::class.java.simpleName
)