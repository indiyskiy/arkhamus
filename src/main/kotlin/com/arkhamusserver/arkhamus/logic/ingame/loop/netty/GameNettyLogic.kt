package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.UnknownExceptionResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.handler.NettyExceptionHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameNettyLogic(
    private val requestHandlers: List<NettyRequestHandler>,
    private val responseMappers: List<NettyResponseMapper>,
    private val exceptionHandlers: List<NettyExceptionHandler>,
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameNettyLogic::class.java)
    }

    fun process(
        requestData: NettyRequestMessage,
        channel: ArkhamusChannel
    ): NettyResponseMessage {
        try {
            logger.info("process $requestData")
            val gameResponse = requestHandlers
                .first { it.acceptClass(requestData) && it.accept(requestData) }
                .process(requestData, channel.userAccount, channel.gameSession, channel)
            logger.info("game response $requestData")
            val response = responseMappers.first { it.acceptClass(gameResponse) && it.accept(gameResponse) }
                .process(gameResponse, requestData, channel.userAccount, channel.gameSession, channel.userRole)
            logger.info("netty response $response")
            return response
        } catch (exception: Exception) {
            logger.error("error on processing request", exception)
            return exceptionResponseMessage(exception)
        }
    }

    private fun exceptionResponseMessage(exception: Exception): NettyResponseMessage {
        return exceptionHandlers
            .firstOrNull { it.accept(exception) }
            ?.parse(exception)
            ?: UnknownExceptionResponseMessage(exception.message ?: "")
    }

}