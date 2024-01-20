package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.json.JsonObjectDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JsonRequestDecoder : JsonObjectDecoder() {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(JsonRequestDecoder::class.java)
    }
    override fun decode(ctx: ChannelHandlerContext, inBuff: ByteBuf?, out: MutableList<Any>) {
        logger.info("JsonRequestDecoder decode")
        super.decode(ctx, inBuff, out)
    }
}