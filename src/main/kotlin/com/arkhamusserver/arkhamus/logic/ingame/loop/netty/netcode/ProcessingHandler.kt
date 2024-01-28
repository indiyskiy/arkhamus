package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.GameNettyLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.entity.ChannelNotFoundException
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.google.gson.Gson
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Sharable
class ProcessingHandler(
    private val gameNettyLogic: GameNettyLogic
) : SimpleChannelInboundHandler<NettyRequestMessage>() {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ProcessingHandler::class.java)
    }

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.debug("Client joined")
        val arkhamusChannel = ArkhamusChannel(
            channel = ctx.channel(),
            channelId = ctx.channel().id().asLongText()
        )
        channelRepository.put(arkhamusChannel)
    }

    override fun channelRead0(context: ChannelHandlerContext, requestData: NettyRequestMessage) {
        val id = context.channel().id().asLongText()
        val arkhamusChannel = channelRepository.get(id) ?: throw ChannelNotFoundException(id)

        val nettyResponse = gameNettyLogic.process(requestData, arkhamusChannel)
        val responseJson = Gson().toJson(nettyResponse)

        logger.debug("Server received - $responseJson")
//        channelRepository.forEach { channel ->
        logger.debug("write back - $responseJson")
        arkhamusChannel.channel.writeAndFlush(responseJson)
        logger.debug("write back - done")
//        }
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