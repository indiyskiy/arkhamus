package com.arkhamusserver.arkhamus.view.dto.netty.request

class AuthRequestMessage(
    var token: String,
    type: String,
) : NettyRequestMessage(type)