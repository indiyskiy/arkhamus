package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.JsonToObjectRequestDecoder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ProcessingHandler
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringEncoder
import org.springframework.stereotype.Component

@Component
class ArkhamusChannelInitializer(
    private val parsers: List<NettyRequestJsonParser>,
    private val processingHandler: ProcessingHandler
) : ChannelInitializer<SocketChannel>() {

    companion object {
        const val MAX_LENGTH = 2 * 1024 * 1024
        private val logger = LoggingUtils.getLogger<ArkhamusChannelInitializer>()
    }

    override fun initChannel(socketChannel: SocketChannel) {
        try {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_NETTY_SYSTEM
            ) {
                logger.info("initializing netty channels with ${parsers.size} decoders")
            }
            val pipeline = socketChannel.pipeline()
            pipeline.addLast(DelimiterBasedFrameDecoder(MAX_LENGTH, *Delimiters.lineDelimiter()))
            pipeline.addLast(JsonToObjectRequestDecoder(parsers))
            pipeline.addLast(StringEncoder())
            pipeline.addLast(processingHandler)
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_NETTY_SYSTEM
            ) {
                logger.info("initialized netty channels")
            }
        } catch (e: Exception) {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_NETTY_SYSTEM
            ) {
                logger.error("Error initializing channel", e)
            }
            socketChannel.close()
        }
    }

}
