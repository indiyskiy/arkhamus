package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.model.netty.messages.ChatMessage
import com.arkhamusserver.arkhamus.model.netty.messages.EmptyMessage
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.shaded.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class JsonToObjectRequestDecoder : MessageToMessageDecoder<ByteBuf>() {
    var mapper: ObjectMapper = ObjectMapper()
    var gson = Gson()

    private val charset: Charset = Charset.forName("UTF-8")
    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
        val jsonString = msg.toString(charset)
        val mainNode: JsonNode = mapper.readTree(jsonString)
        val parsed = when (
            val type: String = mainNode.get("type").asText()
        ) {
            "ChatMessage" -> gson.fromJson(jsonString, ChatMessage::class.java)
            else -> EmptyMessage().apply { this.type = type }
        }
        out.add(parsed)
    }
}