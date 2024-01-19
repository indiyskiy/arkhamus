package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.json.JsonObjectDecoder
import org.springframework.stereotype.Component

@Component
class JsonRequestDecoder : JsonObjectDecoder() {

    override fun decode(ctx: ChannelHandlerContext, inBuff: ByteBuf?, out: MutableList<Any>) {
        println("JsonRequestDecoder decode")
        super.decode(ctx, inBuff, out)
    }
}