package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.UnknownExceptionResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.handler.NettyExceptionHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameNettyLogic(
    private val exceptionHandlers: List<NettyExceptionHandler>,
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameNettyLogic::class.java)
    }

    fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ) {
        try {
//        if(nettyTickRequestMessageContainer.)
//            val requestData = nettyTickRequestMessageContainer.nettyRequestMessage
//            val channel = nettyTickRequestMessageContainer.arkhamusChannel
//            logger.info("process ${requestData}")
//            val gameResponse = requestHandlers
//                .first {
//                    it.acceptClass(requestData) && it.accept(requestData)
//                }
//                .process(nettyTickRequestMessageContainer)
//            logger.info("game response $requestData")
//            val response = responseMappers.first { it.acceptClass(gameResponse) && it.accept(gameResponse) }
//                .process(gameResponse, requestData, channel.userAccount, channel.gameSession, channel.userRole)
//            logger.info("netty response $response")
//            return response
        } catch (exception: Exception) {
            logger.error("error on processing request", exception)
//            return exceptionResponseMessage(exception)
        }
    }

}