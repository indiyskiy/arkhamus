package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.AuthState

data class NettyResponseAuth (
    val message: AuthState = AuthState.FAIL,
    val tick: Long,
    val userId: Long,
    val gameUser: GameUserResponseMessage,
): NettyResponseMessage {
    override fun tick(): Long = tick
    override fun userId(): Long = userId
    override fun currentUser(): GameUserResponseMessage = gameUser
}
