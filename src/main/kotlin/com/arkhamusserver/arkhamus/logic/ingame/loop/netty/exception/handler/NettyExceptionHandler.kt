package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.handler

import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage

interface NettyExceptionHandler {
    fun accept(exception: Exception): Boolean
    fun parse(exception: Exception): NettyResponseMessage
}