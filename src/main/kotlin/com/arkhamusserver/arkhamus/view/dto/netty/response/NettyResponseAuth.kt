package com.arkhamusserver.arkhamus.view.dto.netty.response

import com.arkhamusserver.arkhamus.model.enums.AuthState

data class NettyResponseAuth(
    val message: AuthState = AuthState.FAIL,
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponseMessage,
    val allGameUsers: List<GameUserResponseMessage>,
) : NettyResponseMessage {
    override fun tick(): Long = tick
    override fun userId(): Long = userId
    override fun myGameUser(): MyGameUserResponseMessage = myGameUser
    override fun allGameUsers(): List<GameUserResponseMessage> = allGameUsers
}
