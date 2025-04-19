package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

@Sharable
class JsonToObjectRequestDecoder(
    private val parsers: List<NettyRequestJsonParser>,
) : MessageToMessageDecoder<ByteBuf>() {

    companion object {
        private val logger = LoggingUtils.getLogger<JsonToObjectRequestDecoder>()
        private val charset: Charset = Charset.forName("UTF-8")
    }

    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
        try {
            val jsonString = msg.toString(charset)
            val mainNode: JsonNode = ObjectMapper().readTree(jsonString)
            val type: String? = mainNode.get("type")?.asText()
            type?.let {
                val parsed = parsers.firstOrNull {
                    it.acceptType(type)
                }
                    ?.getDecodeClass()
                    ?.let {
                        Gson().fromJson(jsonString, it)
                    }
                if (parsed != null) {
                    out.add(parsed)
                } else {
                    logger.error("did not parse request $jsonString")
                    ctx?.close()?.sync()
                    logger.error("${ctx?.channelId()} is closed for sent unknown json")
                }
            } ?: logger.error("can't parse type from $jsonString")
        } catch (e: Exception) {
            try {
                logger.error("Error decoding JSON message ${msg.toString(charset)}")
            } catch (e: Exception) {
                logger.error("Error decoding JSON message")
            } finally {
                ctx?.close()?.sync()
                logger.error("${ctx?.channelId()} is closed for sent non-json data")
            }
        }
    }

}