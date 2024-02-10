package com.arkhamusserver.arkhamus.view.dto.netty.response

data class ContainerNettyResponse(
    var containerCells: List<NettyContainerCell> = emptyList(),
    val tick: Long,
    val userId: Long
) : NettyResponseMessage {
    override fun tick(): Long = tick
    override fun userId(): Long = userId

}