package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.globalutils.toJson
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class ResponseSendingLoopManagerImpl(
    val channelRepository: ChannelRepository
) : ResponseSendingLoopManager {
    private val taskExecutor: ThreadPoolTaskExecutor = ThreadPoolTaskExecutor()

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ResponseSendingLoopManager::class.java)
    }

    init {
        taskExecutor.corePoolSize = 3
        taskExecutor.maxPoolSize = 10
        taskExecutor.queueCapacity = 8
        taskExecutor.keepAliveSeconds = 60
        taskExecutor.setThreadNamePrefix("ResponseSendingExecutor-")
        taskExecutor.initialize()
    }

    override fun addResponses(
        responses: List<NettyResponse>,
        gameId: Long
    ) {
        taskExecutor.execute {
            sendMessages(responses)
        }
    }

    private fun sendMessages(responses: List<NettyResponse>) {
        responses.forEach { responseMessage ->
            sendOneMessage(responseMessage)
        }
    }

    private fun sendOneMessage(responseMessage: NettyResponse) {
        try {
            val channel = channelRepository.getUserChannel(responseMessage.userId)
            channel?.channel?.let {
                if (it.isActive) {
                    logger.warn("channel is not active for user ${responseMessage.userId} channel ${channel.channelId}")
                    return
                }
                if (it.isWritable) {
                    logger.warn("channel is not writeable for user ${responseMessage.userId} channel ${channel.channelId}")
                    return
                }
                it.writeAndFlush(
                    responseMessage.toJson()
                )
            } ?: { logger.warn("channel null for user ${responseMessage.userId}") }
        } catch (th: Throwable) {
            logger.error("failed to send message to user ${responseMessage.userId}", th)
        }
    }
}
