package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.debug.ResponseData
import com.arkhamusserver.arkhamus.model.netty.messages.NettyMessage
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class ProcessingHandler : SimpleChannelInboundHandler<NettyMessage>() {

    companion object {
        // List of connected client channels.
        val channels: MutableList<Channel> = ArrayList()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        println("Client joined - $ctx")
        channels.add(ctx.channel())
    }

    override fun channelRead0(context: ChannelHandlerContext, requestData: NettyMessage) {
        val responseData = ResponseData()
        responseData.message = requestData.toString()

        println("Server received - ${responseData.message}")
        for (channel in channels) {
            println("write back - ${responseData.message}")
            channel.writeAndFlush("-> ${responseData.message}\n")
            println("write back - done")
        }
    }

    /*
	 * In case of exception, close channel. One may chose to custom handle exception
	 * & have alternative logical flows.
	 */
    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        println("Closing connection for client - $ctx")
        println(cause)
        ctx.close()
    }
}