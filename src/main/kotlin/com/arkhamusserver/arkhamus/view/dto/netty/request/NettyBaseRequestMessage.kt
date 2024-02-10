package com.arkhamusserver.arkhamus.view.dto.netty.request

interface NettyBaseRequestMessage : NettyRequestMessage {
    fun baseRequestData(): BaseRequestData
}