package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser

import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage

interface NettyRequestJsonParser {
    fun acceptType(type: String): Boolean =
        type == getDecodeClass().simpleName

    fun getDecodeClass(): Class<out NettyRequestMessage>
}