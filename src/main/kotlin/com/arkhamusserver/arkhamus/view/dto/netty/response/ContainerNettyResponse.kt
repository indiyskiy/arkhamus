package com.arkhamusserver.arkhamus.view.dto.netty.response

data class ContainerNettyResponse(
    var containerCells: List<NettyContainerCell> = emptyList(),
    val tick: Long,
    val userId: Long,
    val myGameUser: MyGameUserResponseMessage,
    val allGameUser: List<GameUserResponseMessage>
) : NettyResponseMessage {
    override fun tick(): Long = tick
    override fun userId(): Long = userId
    override fun myGameUser(): MyGameUserResponseMessage = myGameUser
    override fun allGameUsers(): List<GameUserResponseMessage> = allGameUser

}