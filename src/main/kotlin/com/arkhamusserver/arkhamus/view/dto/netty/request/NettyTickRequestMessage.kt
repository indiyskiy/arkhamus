package com.arkhamusserver.arkhamus.view.dto.netty.request

interface NettyTickRequestMessage: NettyRequestMessage {
    fun gameId(): Long
    fun userId(): Long
    fun tick(): Long
}