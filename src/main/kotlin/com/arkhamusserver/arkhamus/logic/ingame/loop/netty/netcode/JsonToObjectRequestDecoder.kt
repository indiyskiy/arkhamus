package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.jsonparser.NettyRequestJsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.Charset

class JsonToObjectRequestDecoder(
    private val parsers: List<NettyRequestJsonParser>,
) : MessageToMessageDecoder<ByteBuf>() {
    private var mapper: ObjectMapper = ObjectMapper()
    private val gson = Gson()

    private val charset: Charset = Charset.forName("UTF-8")

    companion object {
        val logger: Logger = LoggerFactory.getLogger(JsonToObjectRequestDecoder::class.java)
    }

    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
        try {
            val jsonString = msg.toString(charset)
            val mainNode: JsonNode = mapper.readTree(jsonString)
            val type: String? = mainNode.get("type")?.asText()
            type?.let {
                val parsed = parsers.firstOrNull {
                    it.acceptType(type)
                }
                    ?.getDecodeClass()
                    ?.let {
                        gson.fromJson(jsonString, it)
                    }
                if (parsed != null) {
                    out.add(parsed)
                } else {
                    logger.error("did not parse request $jsonString")
                }
            }?: logger.error("can't parse type from $jsonString")
        } catch (e: Exception) {
            try {
                logger.error("Error decoding JSON message ${msg.toString(charset)}", e)
            } catch (e: Exception){
                logger.error("Error decoding JSON message", e)
            }
        }
    }

}