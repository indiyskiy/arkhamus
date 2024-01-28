package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage

data class UnknownExceptionResponseMessage(
    private val errorMessage: String
): NettyResponseMessage