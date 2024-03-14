package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.globalutils.toJson
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class ResponseSendingLoopManagerImpl(
    val channelRepository: ChannelRepository
): ResponseSendingLoopManager {
    private val taskExecutor: ThreadPoolTaskExecutor = ThreadPoolTaskExecutor()

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ResponseSendingLoopManager::class.java)
    }

    init {
        taskExecutor.corePoolSize = 3
        taskExecutor.maxPoolSize = 5
        taskExecutor.initialize()
    }

    override fun addResponses(
        responses: List<NettyResponseMessage>,
        gameId: Long
    ) {
        taskExecutor.execute {
            sendMessages(responses)
        }
    }

    private fun sendMessages(responses: List<NettyResponseMessage>) {
        responses.forEach { responseMessage ->
            sendOneMessage(responseMessage)
        }
    }

    private fun sendOneMessage(responseMessage: NettyResponseMessage) {
        val channel = channelRepository.getUserChannel(responseMessage.userId)
        channel?.channel?.writeAndFlush(
            responseMessage.toJson()
        )
    }
}
