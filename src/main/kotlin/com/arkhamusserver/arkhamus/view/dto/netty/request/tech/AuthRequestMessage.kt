package com.arkhamusserver.arkhamus.view.dto.netty.request.tech

import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage

class AuthRequestMessage(
    var token: String,
    type: String,
) : NettyRequestMessage(type)