package com.arkhamusserver.arkhamus.view.dto.netty.request

data class EmptyMessage(
    var type: String,
    var tick: Long
) : NettyTickRequestMessage {
    override fun tick(): Long {
        return tick
    }
}