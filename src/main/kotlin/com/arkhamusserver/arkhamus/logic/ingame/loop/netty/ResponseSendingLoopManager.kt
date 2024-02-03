package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessageContainer
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class ResponseSendingLoopManager(
    val channelRepository: ChannelRepository
) {
    private val responseMap: ConcurrentMap<Long, NettyResponseContainer> = ConcurrentHashMap()

    val gson = Gson()

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ResponseSendingLoopManager::class.java)
    }

    fun addResponse(
        response: NettyResponseMessage,
        userId: Long,
        tick: Long,
        game: RedisGame,
        gameId: Long,
        channelId: String
    ) {
        logger.info("add response ${response.javaClass.simpleName}")
        var responseContainer = responseMap[gameId]
        if (responseContainer == null) {
            responseContainer = NettyResponseContainer()
            responseMap[gameId] = responseContainer
        }
        responseContainer.put(
            NettyResponseMessageContainer(
                response,
                userId,
                channelId
            )
        )
    }

    fun flush(tick: Long, game: RedisGame, gameId: Long) {
        logger.info("FLUSH! ${responseMap.size}")
        val nettyResponseContainer = responseMap[gameId]
        nettyResponseContainer?.let { responseContainer ->
            responseContainer.get(tick).forEach { messageResponseContainer ->
                val channel = channelRepository.getChannel(messageResponseContainer.channelId)
                channel?.writeAndFlush(
                    gson.toJson(
                        messageResponseContainer.nettyResponseMessage
                    )
                )
            }
        }
    }
}