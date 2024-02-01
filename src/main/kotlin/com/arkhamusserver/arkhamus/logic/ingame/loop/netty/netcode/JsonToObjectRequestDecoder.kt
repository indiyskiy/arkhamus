package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.EmptyMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.GetContainerRequestMessage
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import java.nio.charset.Charset

class JsonToObjectRequestDecoder : MessageToMessageDecoder<ByteBuf>() {
    var mapper: ObjectMapper = ObjectMapper()
    val gson = Gson()

    private val charset: Charset = Charset.forName("UTF-8")
    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: MutableList<Any>) {
        val jsonString = msg.toString(charset)
        val mainNode: JsonNode = mapper.readTree(jsonString)
        val type: String = mainNode.get("type").asText()
        val tick: Long = mainNode.get("baseRequestData").get("tick").asLong()
        val parsed = when (type) {
            AuthRequestMessage::class.java.simpleName -> {
                gson.fromJson(jsonString, AuthRequestMessage::class.java)
            }

            GetContainerRequestMessage::class.java.simpleName -> {
                gson.fromJson(
                    jsonString,
                    GetContainerRequestMessage::class.java
                )
            }

            else -> {
                EmptyMessage(type, tick).apply { this.type = type }
            }
        }
        out.add(parsed)
    }

}