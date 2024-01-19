package com.arkhamusserver.arkhamus.model.netty.messages

data class ChatMessage(
    private var message: String?,
    private var user: String?,
    private var type: String
) : NettyMessage