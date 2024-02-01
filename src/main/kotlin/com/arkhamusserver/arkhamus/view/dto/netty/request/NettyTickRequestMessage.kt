package com.arkhamusserver.arkhamus.view.dto.netty.request

interface NettyTickRequestMessage:NettyRequestMessage  {
    fun tick(): Long
}