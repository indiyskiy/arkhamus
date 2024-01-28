package com.arkhamusserver.arkhamus.config.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.JsonToObjectRequestDecoder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ProcessingHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ArkhamusChannelInitializer : ChannelInitializer<SocketChannel>() {

    @Autowired
    lateinit var processingHandler: ProcessingHandler

    companion object {
        const val MAX_LENGTH = 1024 * 1024
    }

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(DelimiterBasedFrameDecoder(MAX_LENGTH, *Delimiters.lineDelimiter()))
        pipeline.addLast(JsonToObjectRequestDecoder())
        pipeline.addLast(StringEncoder())
        pipeline.addLast(processingHandler)
    }

}
