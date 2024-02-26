package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.JsonToObjectRequestDecoder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ProcessingHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ArkhamusChannelInitializer : ChannelInitializer<SocketChannel>() {

    @Autowired
    lateinit var processingHandler: ProcessingHandler

    @Autowired
    lateinit var parsers: List<NettyRequestJsonParser>

    companion object {
        const val MAX_LENGTH = 100 * 1024 * 1024
        var logger: Logger = LoggerFactory.getLogger(ArkhamusChannelInitializer::class.java)
    }

    override fun initChannel(socketChannel: SocketChannel) {
        try {
            logger.info("initializing netty channels with ${parsers.size} decoders")
            val pipeline = socketChannel.pipeline()
            pipeline.addLast(DelimiterBasedFrameDecoder(MAX_LENGTH, *Delimiters.lineDelimiter()))
            pipeline.addLast(JsonToObjectRequestDecoder(parsers))
            pipeline.addLast(StringEncoder())
            pipeline.addLast(processingHandler)
            logger.info("initialized netty channels")
        } catch (e: Exception) {
            logger.error("Error initializing channel", e)
            socketChannel.close()
        }
    }

}
