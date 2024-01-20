package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.GameNettyLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.google.gson.Gson
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProcessingHandler(
    private val gameNettyLogic: GameNettyLogic
) : SimpleChannelInboundHandler<NettyRequestMessage>() {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(JsonRequestDecoder::class.java)
    }

    private val channels: MutableList<ArkhamusChannel> = ArrayList()
    private val gson = Gson()

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.debug("Client joined")
        channels.add(
            ArkhamusChannel().apply {
                this.channel = ctx.channel()
                this.channelId = ctx.channel().id().asLongText()
            }
        )
    }

    override fun channelRead0(context: ChannelHandlerContext, requestData: NettyRequestMessage) {
        val id = context.channel().id().asLongText()
        val channel = channels.first { it.channelId == id }

        val nettyResponse = gameNettyLogic.process(requestData, channel)
        val responseJson = gson.toJson(nettyResponse)

        logger.debug("Server received - $responseJson")
        channels.forEach {
            logger.debug("write back - $responseJson")
            it.channel?.writeAndFlush(responseJson)
            logger.debug("write back - done")
        }
    }


    /*
	 * In case of exception, close channel. One may choose to custom handle exception
	 * & have alternative logical flows.
	 */
    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("Closing connection for client - $ctx")
        logger.error("Closing connection exception", cause)
        ctx.close()
    }
}