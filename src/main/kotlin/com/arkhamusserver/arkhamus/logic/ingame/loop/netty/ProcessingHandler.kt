package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.debug.ResponseData
import com.arkhamusserver.arkhamus.model.netty.messages.NettyMessage
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessingHandler : SimpleChannelInboundHandler<NettyMessage>() {

    companion object {
        // List of connected client channels.
        val channels: MutableList<Channel> = ArrayList()
        var logger: Logger = LoggerFactory.getLogger(JsonRequestDecoder::class.java)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.debug("Client joined")
        channels.add(ctx.channel())
    }

    override fun channelRead0(context: ChannelHandlerContext, requestData: NettyMessage) {
        val responseData = ResponseData()
        responseData.message = requestData.toString()

        logger.debug("Server received - ${responseData.message}")
        for (channel in channels) {
            logger.debug("write back - ${responseData.message}")
            channel.writeAndFlush("-> ${responseData.message}\n")
            logger.debug("write back - done")
        }
    }

    /*
	 * In case of exception, close channel. One may chose to custom handle exception
	 * & have alternative logical flows.
	 */
    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("Closing connection for client - $ctx")
        logger.error("Closing connection exception", cause)
        ctx.close()
    }
}