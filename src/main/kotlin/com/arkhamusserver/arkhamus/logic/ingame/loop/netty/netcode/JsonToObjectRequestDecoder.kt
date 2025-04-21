package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils.EVENT_NETTY_SYSTEM
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
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
                    LoggingUtils.withContext(
                        eventType = EVENT_NETTY_SYSTEM
                    ) {
                        logger.debug("did not parse request $jsonString")
                    }
                    ctx?.close()?.sync()
                    LoggingUtils.withContext(
                        eventType = EVENT_NETTY_SYSTEM
                    ) {
                        logger.debug("${ctx?.channelId()} is closed for sent unknown json")
                    }
                }
            } ?: {
                LoggingUtils.withContext(
                    eventType = EVENT_NETTY_SYSTEM
                ) {
                    logger.error("can't parse type from $jsonString")
                }
            }
        } catch (e: Exception) {
            try {
                LoggingUtils.withContext(
                    eventType = EVENT_NETTY_SYSTEM
                ) {
                    logger.debug("Error decoding JSON message ${msg.toString(charset)}")
                }
            } catch (e: Exception) {
                LoggingUtils.withContext(
                    eventType = EVENT_NETTY_SYSTEM
                ) {
                    logger.debug("Error decoding JSON message", e)
                }
            } finally {
                ctx?.close()?.sync()
                LoggingUtils.withContext(
                    eventType = EVENT_NETTY_SYSTEM
                ) {
                    logger.debug("${ctx?.channelId()} is closed for sent non-json data", e)
                }
            }
        }
    }

}