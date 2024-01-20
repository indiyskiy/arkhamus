package com.arkhamusserver.arkhamus.view.dto.netty.request

data class AuthRequestMessage(
    var type: String,
    var token: String?
) : NettyRequestMessage